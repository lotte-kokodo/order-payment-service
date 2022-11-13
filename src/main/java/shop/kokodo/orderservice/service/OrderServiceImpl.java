package shop.kokodo.orderservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.kokodo.orderservice.dto.request.CartOrderDto;
import shop.kokodo.orderservice.dto.request.SingleProductOrderDto;
import shop.kokodo.orderservice.dto.response.OrderDetailInformationDto;
import shop.kokodo.orderservice.dto.response.OrderInformationDto;
import shop.kokodo.orderservice.dto.response.OrderProductThumbnailDto;
import shop.kokodo.orderservice.entity.Cart;
import shop.kokodo.orderservice.entity.Order;
import shop.kokodo.orderservice.entity.OrderProduct;
import shop.kokodo.orderservice.entity.enums.status.CartStatus;
import shop.kokodo.orderservice.feign.client.MemberServiceClient;
import shop.kokodo.orderservice.feign.client.ProductServiceClient;
import shop.kokodo.orderservice.feign.client.PromotionServiceClient;
import shop.kokodo.orderservice.feign.response.OrderMemberDto;
import shop.kokodo.orderservice.feign.response.OrderProductDto;
import shop.kokodo.orderservice.feign.response.ProductThumbnailDto;
import shop.kokodo.orderservice.feign.response.RateCouponDto;
import shop.kokodo.orderservice.feign.response.RateDiscountPolicyDto;
import shop.kokodo.orderservice.messagequeue.KafkaProducer;
import shop.kokodo.orderservice.repository.interfaces.CartRepository;
import shop.kokodo.orderservice.repository.interfaces.OrderProductRepository;
import shop.kokodo.orderservice.repository.interfaces.OrderRepository;
import shop.kokodo.orderservice.service.interfaces.OrderService;
import shop.kokodo.orderservice.service.utils.ProductPriceCalculator;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {



    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderProductRepository orderProductRepository;

    private final ProductPriceCalculator productPriceCalculator;

    // Feign Service
    private final ProductServiceClient productServiceClient;
    private final MemberServiceClient memberServiceClient;
    private final PromotionServiceClient promotionServiceClient;

    //CircuitBreaker
    private final CircuitBreakerFactory circuitBreakerFactory;

    //Kafka
    private final KafkaProducer kafkaProducer;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            ProductServiceClient productServiceClient,
            MemberServiceClient memberServiceClient,
            OrderProductRepository orderProductRepository,
            PromotionServiceClient promotionServiceClient,
            ProductPriceCalculator productPriceCalculator,
            CircuitBreakerFactory circuitBreakerFactory,
            KafkaProducer kafkaProducer) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productPriceCalculator = productPriceCalculator;
        this.orderProductRepository = orderProductRepository;
        this.productServiceClient = productServiceClient;
        this.memberServiceClient = memberServiceClient;
        this.promotionServiceClient = promotionServiceClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional(readOnly = false)
    public Order orderSingleProduct(SingleProductOrderDto dto) {
        Long productId = dto.getProductId();
        Long memberId = dto.getMemberId();
        Integer qty = dto.getQty();

        // 상품 가격
        OrderProductDto orderProductDto = productServiceClient.getSingleOrderProduct(productId);

        // 주문 상품 생성
        List<OrderProduct> orderProducts = List.of(OrderProduct.createOrderProduct(dto, orderProductDto));

        // 사용자 이름, 주소
        OrderMemberDto orderMemberDto = memberServiceClient.getOrderMember(dto.getMemberId());

        // [promotion-service feign]
        // 비율할인정책, 고정할인정책, 비율쿠폰, 고정쿠폰 조회
        Long sellerId = dto.getSellerId();
        Map<Long, RateDiscountPolicyDto> rateDiscountProductMap = promotionServiceClient.getRateDiscountPolicy(List.of(productId));
        Map<Long, Boolean> fixDiscountPolicySellerMap = promotionServiceClient.getFixDiscountPolicyStatusForFeign(List.of(productId), List.of(sellerId));

        Long rateCouponId = dto.getRateCouponId();
        Long fixCouponId = dto.getFixCouponId();
        Map<Long, RateCouponDto> rateCouponMap = (rateCouponId != null) ?  promotionServiceClient.findRateCouponByCouponIdList(List.of(rateCouponId)) : new LinkedHashMap<>();
        List<Long> fixCouponSellerIds = (fixCouponId != null) ? promotionServiceClient.findFixCouponByCouponIdList(List.of(fixCouponId)) : new ArrayList<>();

        // 주문총액
        // 비율할인정책, 비율할인쿠폰 적용
        Map<Long, Long> productSellerMap = new HashMap<>(){{ put(productId, sellerId); }};
        Integer totalPrice = productPriceCalculator.calcTotalPrice(orderProducts, productSellerMap, rateDiscountProductMap, fixDiscountPolicySellerMap, rateCouponMap, fixCouponSellerIds);

        // 주문 생성
        Order order = Order.createOrder(memberId, orderMemberDto.getName(), orderMemberDto.getAddress(), totalPrice, orderProducts);
        orderRepository.save(order);

        kafkaProducer.send("product-decrease-stock", new LinkedHashMap<>() {{
            put(productId, qty);
        }});

        // TODO: 주문 완료 후 할인쿠폰 사용 처리 Kafka
//        kafkaProducer.send("promotion-coupon-status", List.of(couponId));

        return order;
    }

    @Transactional(readOnly = false)
    public Order orderCartProducts(CartOrderDto dto) {

        Map<Long, Long> productSellerMap = dto.getProductSellerMap();
        List<Long> rateCouponIds = dto.getRateCouponIds();
        List<Long> fixCouponIds = dto.getFixCouponIds();

        // '장바구니상품' 조회
        List<Cart> carts = cartRepository.findByIdIn(dto.getCartIds());

        List<Long> cartProductIds = carts.stream().map(Cart::getProductId).collect(Collectors.toList());
        Map<Long, OrderProductDto> productPriceMap = productServiceClient.getCartOrderProduct(cartProductIds);

        // 주문 상품 생성
        List<OrderProduct> orderProducts = carts.stream()
            .map((cart) -> OrderProduct.createOrderProduct(cart, productPriceMap.get(cart.getProductId())))
            .collect(Collectors.toList());


        List<Long> productIds = new ArrayList<>();
        List<Long> sellerIds = new ArrayList<>();
        productSellerMap.keySet().forEach((productId) -> {
            productIds.add(productId);
            sellerIds.add(productSellerMap.get(productId));
        });

        // [promotion-service feign]
        // 비율할인정책, 고정할인정책, 비율쿠폰, 고정쿠폰 조회
        Map<Long, RateDiscountPolicyDto> rateDiscountProductMap = promotionServiceClient.getRateDiscountPolicy(productIds);
        Map<Long, Boolean> fixDiscountPolicySellerMap = promotionServiceClient.getFixDiscountPolicyStatusForFeign(productIds, sellerIds);
        Map<Long, RateCouponDto> rateCouponMap = promotionServiceClient.findRateCouponByCouponIdList(rateCouponIds);
        List<Long> fixCouponSellerIds = promotionServiceClient.findFixCouponByCouponIdList(fixCouponIds);

        // 주문 총 가격 계산
        Integer totalPrice = productPriceCalculator.calcTotalPrice(orderProducts, productSellerMap, rateDiscountProductMap, fixDiscountPolicySellerMap, rateCouponMap, fixCouponSellerIds);

        // 사용자 이름, 주소
        Long memberId = dto.getMemberId();
        OrderMemberDto orderMemberDto = memberServiceClient.getOrderMember(memberId);

        Order order = Order.createOrder(memberId, orderMemberDto.getName(), orderMemberDto.getAddress(), totalPrice, orderProducts);
        orderRepository.save(order);

        // 장바구니 상태 업데이트
        carts.forEach((cart -> cart.changeStatus(CartStatus.ORDER_PROCESS)));
        cartRepository.saveAll(carts);

        // 상품 재고 감소
        Map<Long, Integer> productIdQtyMap = carts.stream()
                .collect(Collectors.toMap(Cart::getProductId, Cart::getQty));
        kafkaProducer.send("product-decrease-stock", productIdQtyMap);

        // TODO: 주문 완료 후 할인쿠폰 사용 처리 Kafka
//        kafkaProducer.send("promotion-coupon-status", couponIds);

        return order;
    }

    @Transactional(readOnly = false)
    @Override
    public List<OrderInformationDto> getOrderList(Long memberId) {
        List<Order> orderList = orderRepository.findAllByMemberId(memberId);

        List<OrderProductThumbnailDto> orderProductThumbnailDtoList = orderProductRepository.findAllByOrderIdIn(
                orderList.stream()
                        .map(Order::getId)
                        .collect(Collectors.toList()
                        )
        );

        List<Long> productIdList = orderProductThumbnailDtoList.stream()
                .map(OrderProductThumbnailDto::getProductId)
                .collect(Collectors.toList());

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        Map<Long, ProductThumbnailDto> productList = circuitBreaker.run(
                () -> productServiceClient.getProductList(productIdList),
                throwable -> new HashMap<Long, ProductThumbnailDto>()
        );

        List<OrderInformationDto> response = new ArrayList<>();
        for (int i=0;i< orderProductThumbnailDtoList.size();i++) {
            Long productId = productIdList.get(i);
            ProductThumbnailDto product = productList.get(productId);

            response.add(OrderInformationDto.builder()
                    .orderId(orderList.get(i).getId())
                    .name(product.getName() + " 외 " + orderProductThumbnailDtoList.get(i).getCount() + "건")
                    .orderStatus(orderList.get(i).getOrderStatus())
                    .price(orderList.get(i).getTotalPrice())
                    .thumbnail(product.getThumbnail())
                    .orderDate(orderList.get(i).getOrderDate())
                    .build()
            );
        }
        log.info("response : " + response);
        return response;
    }

    @Transactional(readOnly = false)
    @Override
    public List<OrderDetailInformationDto> getOrderDetailList(Long memberId, Long orderId) {
        List<OrderProduct> orderProductList = orderProductRepository.findAllByIdAndMemberId(memberId, orderId);
        log.info("orderProductList : " + orderProductList.toString());

        List<Long> productIdList = orderProductList.stream()
                .map(OrderProduct::getProductId)
                .collect(Collectors.toList());
        log.info("productIdList : " + productIdList.toString());
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        Map<Long, ProductThumbnailDto> productList = circuitBreaker.run(
                () -> productServiceClient.getProductList(productIdList),
                throwable -> new HashMap<Long, ProductThumbnailDto>()
        );

        List<OrderDetailInformationDto> orderDetailInformationDtoList = new ArrayList<>();

        for (int i = 0; i < orderProductList.size(); i++) {
            OrderDetailInformationDto orderDetailInformationDto = OrderDetailInformationDto.builder()
                    .id(orderProductList.get(i).getId())
                    .name(productList.get(productIdList.get(i)).getName())
                    .price(orderProductList.get(i).getUnitPrice())
                    .qty(orderProductList.get(i).getQty())
                    .thumbnail(productList.get(productIdList.get(i)).getThumbnail())
                    .build();
            orderDetailInformationDtoList.add(orderDetailInformationDto);
        }

        return orderDetailInformationDtoList;
    }

}
