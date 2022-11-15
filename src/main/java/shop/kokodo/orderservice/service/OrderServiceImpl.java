package shop.kokodo.orderservice.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import java.util.stream.Collectors;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.kokodo.orderservice.dto.request.CartOrderRequest;
import shop.kokodo.orderservice.dto.request.SingleProductOrderRequest;
import shop.kokodo.orderservice.dto.response.dto.OrderDetailInformationDto;
import shop.kokodo.orderservice.dto.response.dto.OrderInformationDto;
import shop.kokodo.orderservice.dto.response.dto.OrderProductDslDto;
import shop.kokodo.orderservice.dto.response.dto.OrderProductDto;
import shop.kokodo.orderservice.entity.Cart;
import shop.kokodo.orderservice.entity.Order;
import shop.kokodo.orderservice.entity.OrderProduct;
import shop.kokodo.orderservice.entity.QOrderProduct;
import shop.kokodo.orderservice.entity.enums.status.CartStatus;
import shop.kokodo.orderservice.feign.client.MemberServiceClient;
import shop.kokodo.orderservice.feign.client.ProductServiceClient;
import shop.kokodo.orderservice.feign.client.PromotionServiceClient;
import shop.kokodo.orderservice.feign.response.FeignResponse;
import shop.kokodo.orderservice.feign.response.FeignResponse.MemberDeliveryInfo;
import shop.kokodo.orderservice.feign.response.FeignResponse.RateCoupon;
import shop.kokodo.orderservice.feign.response.FeignResponse.RateDiscountPolicy;
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

    //queryDSL
    private final JPAQueryFactory jpaQueryFactory;
    private static QOrderProduct orderProduct = QOrderProduct.orderProduct;

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
            JPAQueryFactory jpaQueryFactory,
            KafkaProducer kafkaProducer) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productPriceCalculator = productPriceCalculator;
        this.orderProductRepository = orderProductRepository;
        this.productServiceClient = productServiceClient;
        this.memberServiceClient = memberServiceClient;
        this.promotionServiceClient = promotionServiceClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.jpaQueryFactory = jpaQueryFactory;
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
    public List<OrderInformationDto> getOrderList(Long memberId) {
        List<Order> orderList = orderRepository.findAllByMemberId(memberId);

        List<Long> orderIdList = orderList.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        List<OrderProductDto> orderProductDtoList = orderProductRepository.findAllByOrderIdIn(orderIdList);

        List<Long> productIdList = orderProductDtoList.stream()
                .map(OrderProductDto::getProductId)
                .collect(Collectors.toList());

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        Map<Long, FeignResponse.Product> productList = circuitBreaker.run(
                () -> productServiceClient.getProductListMap(productIdList),
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
    public List<OrderInformationDto> getOrderListDsl(Long memberId) {
        List<Order> orderList = orderRepository.findAllByMemberId(memberId);

        List<Long> orderIdList = orderList.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        List<OrderProductDslDto> orderProductDtoListDsl = findAllByOrderIdInDsl(orderIdList);

        List<Long> productIdList = orderProductDtoListDsl.stream()
                .map(OrderProductDslDto::getProductId)
                .collect(Collectors.toList());

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        Map<Long, FeignResponse.Product> productList = circuitBreaker.run(
                () -> productServiceClient.getProductListMap(productIdList),
                throwable -> new HashMap<Long, FeignResponse.Product>()
        );

        List<OrderInformationDto> response = new ArrayList<>();
        for (int i=0;i<orderProductDtoListDsl.size();i++) {
            Long productId = productIdList.get(i);
            FeignResponse.Product product = productList.get(productId);

            response.add(OrderInformationDto.builder()
                    .orderId(orderList.get(i).getId())
                    .name(product.getName() + " 외 " + orderProductDtoListDsl.get(i).getCount() + "건")
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
                () -> productServiceClient.getProductListMap(productIdList),
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
                    .orderStatus(orderProductList.get(i).getOrder().getOrderStatus())
                    .build();
            orderDetailInformationDtoList.add(orderDetailInformationDto);
        }

        return orderDetailInformationDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<Long, List<Integer>> getProductAllPrice(List<Long> productIdList) {

        Calendar cal = Calendar.getInstance(Locale.KOREA);
        LocalDateTime startDate = getDate("start");
        LocalDateTime endDate = getDate("end");

        List<OrderProduct> orderPriceDtoList = orderProductRepository.findByProductIdListAndSellerId(productIdList, startDate, endDate);
        Map<Long, List<Integer>> result = new HashMap<>();
        for(OrderProduct orderProduct : orderPriceDtoList) {
            List<Integer> list = new ArrayList<>();
            list.add(orderProduct.getUnitPrice());
            list.add(orderProduct.getQty());
            result.put(orderProduct.getProductId(), list);
        }

        return result;
    }

    LocalDateTime getDate(String flag) {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        //금주 시작 날짜
        if(flag.equals("start")) {
            cal.add(Calendar.DATE, 2 - cal.get(Calendar.DAY_OF_WEEK));
        }
        //금주 종료 날짜
        else if(flag.equals("end")){
            cal.add(Calendar.DATE, 8 - cal.get(Calendar.DAY_OF_WEEK));
        }
        TimeZone tz = cal.getTimeZone();
        ZoneId zoneId = tz.toZoneId();

        return LocalDateTime.ofInstant(cal.toInstant(), zoneId);
    }

    private List<OrderProductDslDto> findAllByOrderIdInDsl(List<Long> orderIdList) {
        orderProduct = QOrderProduct.orderProduct;
        QueryResults<Tuple> results = jpaQueryFactory.select(orderProduct.productId, orderProduct.count(), orderProduct.order.id)
                .from(orderProduct)
                .where(orderProduct.order.id.in(orderIdList))
                .groupBy(orderProduct.order.id)
                .fetchResults();

        List<OrderProductDslDto> result = new ArrayList<>();
        results.getResults().stream().forEach(tuple -> result.add(
                OrderProductDslDto.builder()
                        .productId(tuple.get(0, Long.class))
                        .count(tuple.get(1, Long.class))
                        .orderId(tuple.get(2, Long.class))
                        .build()
                )
        );
        return result;
    }
}
