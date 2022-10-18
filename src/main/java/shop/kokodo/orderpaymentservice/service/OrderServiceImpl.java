package shop.kokodo.orderpaymentservice.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import shop.kokodo.orderpaymentservice.dto.response.data.OrderResponse.GetOrderProduct;
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
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductOfOrder;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.RateDiscountPolicy;
import shop.kokodo.orderpaymentservice.messagequeue.KafkaProducer;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderRepository;
import shop.kokodo.orderpaymentservice.service.interfaces.OrderService;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    // FeignClient
    private final ProductServiceClient productServiceClient;
    private final MemberServiceClient memberServiceClient;
    private final PromotionServiceClient promotionServiceClient;

    private final KafkaProducer kafkaProducer;

    @Autowired
    public OrderServiceImpl(
        OrderRepository orderRepository,
        CartRepository cartRepository,
        ProductServiceClient productServiceClient,
        MemberServiceClient memberServiceClient,
        PromotionServiceClient promotionServiceClient,
        KafkaProducer kafkaProducer) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productServiceClient = productServiceClient;
        this.memberServiceClient = memberServiceClient;
        this.promotionServiceClient = promotionServiceClient;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    public Order orderSingleProduct(Long memberId, Long productId, Integer qty, Long couponId) {

        // 상품 가격
        FeignResponse.ProductPrice productPrice = productServiceClient.getProduct(productId);
        Integer unitPrice = productPrice.getPrice();

        // 주문 상품 생성
        OrderProduct orderProduct = OrderProduct.builder()
            .memberId(memberId)
            .productId(productId)
            .qty(qty)
            .unitPrice(unitPrice)
            .build();

        // 사용자 이름, 주소
        MemberDeliveryInfo memberDeliveryInfo = memberServiceClient.getMemberAddress(memberId);

        // TODO: PromotionService 할인 비율 가져오기
        Map<Long, RateDiscountPolicy> discountProductMap = promotionServiceClient.getRateDiscountPolicy(List.of(productId));
        RateDiscountPolicy rateDiscountPolicy = discountProductMap.get(productId);

        // 주문 생성
        Order order = Order.builder()
            .memberId(memberId)
            .deliveryMemberAddress(memberDeliveryInfo.getAddress())
            .deliveryMemberName(memberDeliveryInfo.getName())
            .totalPrice(isExistRateDiscountPolicy(rateDiscountPolicy) ?
                getDiscPrice(unitPrice, qty, rateDiscountPolicy.getRate()) : unitPrice*qty)
            .orderDate(LocalDateTime.now())
            .orderStatus(OrderStatus.ORDER_SUCCESS)
            .orderProducts(List.of(orderProduct))
            .build();
        orderProduct.setOrder(order);
        orderRepository.save(order);

        kafkaProducer.send("kokodo.product.de-stock", new LinkedHashMap<>(){{ put(productId, qty); }});

        // TODO: 쿠폰 상태 수정 Kafka Listener 토픽 수정
//        kafkaProducer.send("kokodo.coupon.status", List.of(couponId));

        return order;
    }

    @Transactional
    public Order orderCartProducts(Long memberId, List<Long> cartIds, List<Long> couponIds) {
        // '장바구니상품' 조회
        List<Cart> carts = cartRepository.findByIdIn(cartIds);

        // 주문 상품 생성
        List<OrderProduct> orderProducts = carts.stream()
            .map(OrderProduct::convertCartToOrderProduct)
            .collect(Collectors.toList());

        // TODO: PromotionService 할인 비율 가져오기
        // 비율 할인 정책 조회
        List<Long> productIds = carts.stream().map(Cart::getProductId).collect(Collectors.toList());
        Map<Long, RateDiscountPolicy> discountProductMap = promotionServiceClient.getRateDiscountPolicy(productIds);

        // 주문 총 가격 계산
        Integer totalPrice = orderProducts.stream()
            .map(orderProduct -> {
                RateDiscountPolicy rateDiscountPolicy = discountProductMap.get(orderProduct.getProductId());
                Integer unitPrice = orderProduct.getUnitPrice();
                Integer qty = orderProduct.getQty();

                return isExistRateDiscountPolicy(rateDiscountPolicy) ?
                    getDiscPrice(unitPrice, qty, rateDiscountPolicy.getRate()) : unitPrice*qty;
            })
            .mapToInt(Integer::intValue)
            .sum();

        // 사용자 이름, 주소
        MemberDeliveryInfo memberDeliveryInfo = memberServiceClient.getMemberAddress(memberId);

        // 주문 생성
        Order order = Order.builder()
            .deliveryMemberAddress(memberDeliveryInfo.getAddress())
            .deliveryMemberName(memberDeliveryInfo.getName())
            .totalPrice(totalPrice)
            .orderDate(LocalDateTime.now())
            .orderStatus(OrderStatus.ORDER_SUCCESS)
            .memberId(memberId)
            .orderProducts(orderProducts)
            .build();
        orderProducts.forEach((orderProduct -> {orderProduct.setOrder(order);}));
        orderRepository.save(order);

        // 장바구니 상태 업데이트
        carts.forEach((cart -> cart.changeStatus(CartStatus.ORDER_PROCESS)));
        cartRepository.saveAll(carts);

        // 상품 재고 감소
        Map<Long,Integer> productIdQtyMap = carts.stream()
            .collect(Collectors.toMap(Cart::getProductId, Cart::getQty));
        kafkaProducer.send("kokodo.product.de-stock", productIdQtyMap);

        // TODO: 주문 시 사용한 쿠폰 리스트 처리
        // [key] "couponIds"    [value] Long List
        // map.get("couponIds")
//        kafkaProducer.send("kokodo.coupon.status", couponIds);

        return order;
    }

    @Override
    public Map<Long, GetOrderProduct> getOrderSheetProducts(Long memberId, @RequestParam List<Long> productIds) {
        // 주문서 상품 정보 요청
        Map<Long, ProductOfOrder> products = productServiceClient.getOrderProducts(productIds);

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

    private boolean isExistRateDiscountPolicy(RateDiscountPolicy rateDiscountPolicy) {
        return rateDiscountPolicy != null;
    }

    private Integer getDiscPrice(Integer unitPrice, Integer qty, Integer rate) {
        return (int) (unitPrice*qty*(1 - rate*0.01));
    }
}
