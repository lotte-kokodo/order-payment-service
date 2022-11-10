package shop.kokodo.orderpaymentservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import shop.kokodo.orderpaymentservice.dto.request.CartOrderRequest;
import shop.kokodo.orderpaymentservice.dto.request.SingleProductOrderRequest;
import shop.kokodo.orderpaymentservice.dto.response.data.OrderResponse.GetOrderProduct;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderDetailInformationDto;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderInformationDto;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderProductDto;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;
import shop.kokodo.orderpaymentservice.entity.enums.status.CartStatus;
import shop.kokodo.orderpaymentservice.entity.enums.status.OrderStatus;
import shop.kokodo.orderpaymentservice.feign.client.MemberServiceClient;
import shop.kokodo.orderpaymentservice.feign.client.ProductServiceClient;
import shop.kokodo.orderpaymentservice.feign.client.PromotionServiceClient;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.MemberDeliveryInfo;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductPrice;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.RateCoupon;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.RateDiscountPolicy;
import shop.kokodo.orderpaymentservice.feign.response.ProductDto;
import shop.kokodo.orderpaymentservice.messagequeue.KafkaProducer;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderProductRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderRepository;
import shop.kokodo.orderpaymentservice.service.interfaces.OrderService;
import shop.kokodo.orderpaymentservice.service.utils.ProductPriceCalculator;

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
    public Order orderSingleProduct(SingleProductOrderRequest dto) {
        Long productId = dto.getProductId();
        Long memberId = dto.getMemberId();
        Integer qty = dto.getQty();

        // 상품 가격
        FeignResponse.ProductPrice productPrice = productServiceClient.getProductPrice(productId);
        Integer unitPrice = productPrice.getPrice();

        // 주문 상품 생성
        List<OrderProduct> orderProducts = List.of(OrderProduct.createOrderProduct(dto, unitPrice));

        // 사용자 이름, 주소
        MemberDeliveryInfo memberDeliveryInfo = memberServiceClient.getMemberDeliveryInfo(dto.getMemberId());

        // [promotion-service feign]
        // 비율할인정책, 고정할인정책, 비율쿠폰, 고정쿠폰 조회
        Long sellerId = dto.getSellerId();
        Map<Long, RateDiscountPolicy> rateDiscountProductMap = promotionServiceClient.getRateDiscountPolicy(List.of(productId));
        Map<Long, Boolean> fixDiscountPolicySellerMap = promotionServiceClient.getFixDiscountPolicyStatusForFeign(List.of(productId), List.of(sellerId));

        Long rateCouponId = dto.getRateCouponId();
        Long fixCouponId = dto.getFixCouponId();
        Map<Long, RateCoupon> rateCouponMap = (rateCouponId != null) ?  promotionServiceClient.findRateCouponByCouponIdList(List.of(rateCouponId)) : new LinkedHashMap<>();
        List<Long> fixCouponSellerIds = (fixCouponId != null) ? promotionServiceClient.findFixCouponByCouponIdList(List.of(fixCouponId)) : new ArrayList<>();

        // 주문총액
        // 비율할인정책, 비율할인쿠폰 적용
        Map<Long, Long> productSellerMap = new HashMap<>(){{ put(productId, sellerId); }};
        Integer totalPrice = productPriceCalculator.calcTotalPrice(orderProducts, productSellerMap, rateDiscountProductMap, fixDiscountPolicySellerMap, rateCouponMap, fixCouponSellerIds);

        // 주문 생성
        Order order = Order.createOrder(memberId, memberDeliveryInfo.getName(), memberDeliveryInfo.getAddress(), totalPrice, orderProducts);
        orderRepository.save(order);

        kafkaProducer.send("product-decrease-stock", new LinkedHashMap<>() {{
            put(productId, qty);
        }});

        // TODO: 주문 완료 후 할인쿠폰 사용 처리 Kafka
//        kafkaProducer.send("promotion-coupon-status", List.of(couponId));

        return order;
    }

    @Transactional(readOnly = false)
    public Order orderCartProducts(CartOrderRequest dto) {

        Map<Long, Long> productSellerMap = dto.getProductSellerMap();
        List<Long> rateCouponIds = dto.getRateCouponIds();
        List<Long> fixCouponIds = dto.getFixCouponIds();

        // '장바구니상품' 조회
        List<Cart> carts = cartRepository.findByIdIn(dto.getCartIds());

        List<Long> cartProductIds = carts.stream().map(Cart::getProductId).collect(Collectors.toList());
        Map<Long, Integer> productPriceMap = productServiceClient.getProductsPrice(cartProductIds);

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
        Map<Long, RateDiscountPolicy> rateDiscountProductMap = promotionServiceClient.getRateDiscountPolicy(productIds);
        Map<Long, Boolean> fixDiscountPolicySellerMap = promotionServiceClient.getFixDiscountPolicyStatusForFeign(productIds, sellerIds);
        Map<Long, RateCoupon> rateCouponMap = promotionServiceClient.findRateCouponByCouponIdList(rateCouponIds);
        List<Long> fixCouponSellerIds = promotionServiceClient.findFixCouponByCouponIdList(fixCouponIds);

        // 주문 총 가격 계산
        Integer totalPrice = productPriceCalculator.calcTotalPrice(orderProducts, productSellerMap, rateDiscountProductMap, fixDiscountPolicySellerMap, rateCouponMap, fixCouponSellerIds);

        // 사용자 이름, 주소
        Long memberId = dto.getMemberId();
        MemberDeliveryInfo memberDeliveryInfo = memberServiceClient.getMemberDeliveryInfo(memberId);

        Order order = Order.createOrder(memberId, memberDeliveryInfo.getName(), memberDeliveryInfo.getAddress(), totalPrice, orderProducts);
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
    public Map<Long, GetOrderProduct> getOrderSheetProducts(Long memberId, @RequestParam List<Long> productIds) {
        // 주문서 상품 정보 요청
        Map<Long, ProductDto> products = productServiceClient.getOrderProducts(productIds);

        // 할인률 요청
        // 비율 할인 정책 조회
        Map<Long, RateDiscountPolicy> discountProductMap = promotionServiceClient.getRateDiscountPolicy(productIds);

        List<GetOrderProduct> orderProducts = productIds.stream()
                .map(productId -> GetOrderProduct.createGetOrderProduct(products.get(productId),
                        discountProductMap.get(productId)))
                .collect(Collectors.toList());


        return orderProducts.stream().collect(Collectors.toMap(GetOrderProduct::getProductId,
                Function.identity()));
    }

    @Transactional(readOnly = false)
    @Override
    public List<OrderInformationDto> getOrderList(Long memberId) {
        List<Order> orderList = orderRepository.findAllByMemberId(memberId);

        List<OrderProductDto> orderProductDtoList = orderProductRepository.findAllByOrderIdIn(
                orderList.stream()
                        .map(Order::getId)
                        .collect(Collectors.toList()
                        )
        );

        List<Long> productIdList = orderProductDtoList.stream()
                .map(OrderProductDto::getProductId)
                .collect(Collectors.toList());

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        Map<Long, FeignResponse.Product> productList = circuitBreaker.run(
                () -> productServiceClient.getProductList(productIdList),
                throwable -> new HashMap<Long, FeignResponse.Product>()
        );

        List<OrderInformationDto> response = new ArrayList<>();
        for (int i=0;i<orderProductDtoList.size();i++) {
            Long productId = productIdList.get(i);
            FeignResponse.Product product = productList.get(productId);

            response.add(OrderInformationDto.builder()
                    .orderId(orderList.get(i).getId())
                    .name(product.getName() + " 외 " + orderProductDtoList.get(i).getCount() + "건")
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
        Map<Long, FeignResponse.Product> productList = circuitBreaker.run(
                () -> productServiceClient.getProductList(productIdList),
                throwable -> new HashMap<Long, FeignResponse.Product>()
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
