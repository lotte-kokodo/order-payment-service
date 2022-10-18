package shop.kokodo.orderpaymentservice.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import shop.kokodo.orderpaymentservice.dto.response.data.OrderResponse.OrderSheet;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;
import shop.kokodo.orderpaymentservice.entity.enums.EnumMapper;
import shop.kokodo.orderpaymentservice.entity.enums.order.OrderStatus;
import shop.kokodo.orderpaymentservice.feign.client.MemberServiceClient;
import shop.kokodo.orderpaymentservice.feign.client.ProductServiceClient;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.MemberDeliveryInfo;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.MemberOfOrderSheet;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductOfOrderSheet;
import shop.kokodo.orderpaymentservice.messagequeue.KafkaProducer;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderProductRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.ProductRepository;
import shop.kokodo.orderpaymentservice.service.interfaces.OrderService;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    /* feignclient 전 productRepository사용을 위한 repository */
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    // FeignClient
    private final ProductServiceClient productServiceClient;
    private final MemberServiceClient memberServiceClient;

    private final KafkaProducer kafkaProducer;

    @Autowired
    public OrderServiceImpl(
        OrderRepository orderRepository,
        CartRepository cartRepository,
        ProductServiceClient productServiceClient,
        MemberServiceClient memberServiceClient,
        KafkaProducer kafkaProducer),
            OrderProductRepository orderProductRepository) {
            ModelMapper modelMapper,
            OrderRepository orderRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
        KafkaProducer kafkaProducer) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
        this.productServiceClient = productServiceClient;
        this.memberServiceClient = memberServiceClient;
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

        // 주문 생성
        Order order = Order.builder()
            .memberId(memberId)
            .deliveryMemberAddress(memberDeliveryInfo.getAddress())
            .deliveryMemberName(memberDeliveryInfo.getName())
            .totalPrice(unitPrice*qty)
            .orderDate(LocalDateTime.now())
            .orderStatus(OrderStatus.ORDER_SUCCESS)
            .orderProducts(List.of(orderProduct))
            .build();
        orderProduct.setOrder(order);

        orderRepository.save(order);

        kafkaProducer.send("kokodo.product.de-stock", new LinkedHashMap<>(){{ put(productId, qty); }});

        // TODO: 쿠폰 상태 수정 Kafka Listener 토픽 수정
        kafkaProducer.send("kokodo.coupon.status", List.of(couponId));

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

        // 주문 총 가격 계산
        Integer totalPrice = orderProducts.stream()
            .map(orderProduct -> orderProduct.getUnitPrice() * orderProduct.getQty())
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

        // 상품 재고 감소
        // productId (key) - qty (value) Map 생성
        Map<Long,Integer> productIdQtyMap = carts.stream()
            .collect(Collectors.toMap(Cart::getProductId, Cart::getQty));

        kafkaProducer.send("kokodo.product.de-stock", productIdQtyMap);

        // [key] "couponIds"    [value] Long List
        // map.get("couponIds")
        kafkaProducer.send("kokodo.coupon.status", couponIds);

        return order;
    }

    @Override
    public OrderSheet getOrderSheet(Long memberId, @RequestParam List<Long> productIds) {
        // 주문서 상품 정보 요청
        List<ProductOfOrderSheet> products = productServiceClient.getOrderSheetProducts(productIds);

        // 사용자 정보 요청
        MemberOfOrderSheet member = memberServiceClient.getMemberOrderInfo(memberId);

        return OrderSheet.builder()
            .productInfos(products)
            .memberInfo(member)
            .build();
    }
}
