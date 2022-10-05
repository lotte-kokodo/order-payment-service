package shop.kokodo.orderpaymentservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.kokodo.orderpaymentservice.dto.response.dto.MemberResponse;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;
import shop.kokodo.orderpaymentservice.messagequeue.KafkaMessageType;
import shop.kokodo.orderpaymentservice.messagequeue.KafkaProducer;
import shop.kokodo.orderpaymentservice.messagequeue.dto.KafkaDto;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderRepository;
import shop.kokodo.orderpaymentservice.service.interfaces.OrderService;
import shop.kokodo.orderpaymentservice.service.interfaces.client.ProductServiceClient;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final ModelMapper modelMapper;

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    // FeignClient
    private final ProductServiceClient productServiceClient;
//    private final MemberServiceClient memberServiceClient;

    private final KafkaProducer kafkaProducer;

    @Autowired
    public OrderServiceImpl(
        ModelMapper modelMapper,
        OrderRepository orderRepository,
        CartRepository cartRepository,
        ProductServiceClient productServiceClient,
        KafkaProducer kafkaProducer) {

        this.modelMapper = modelMapper;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productServiceClient = productServiceClient;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    public Long orderSingleProduct(Long memberId, Long productId, Integer qty, Long couponId) {

        // TODO: FeignClient 통신 테스트
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


        // TODO: FeignClient 통신 테스트
        // 사용자 이름, 주소
//        MemberResponse memberResponse = memberServiceClient.getMember(memberId);
        MemberResponse memberResponse = new MemberResponse("NaYeon Kwon",
            "서울특별시 강남구 가로수길 43");

        // 주문 생성
        Order order = Order.builder()
            .deliveryMemberName(memberResponse.getMemberName())
            .deliveryMemberAddress(memberResponse.getMemberAddress())
            .totalPrice(unitPrice*qty)
            .orderDate(LocalDateTime.now())
            .orderProducts(List.of(orderProduct))
            .build();

        orderRepository.save(order);

        kafkaProducer.send("kokodo.product.de-stock",
            new KafkaDto.ProductUpdateStockTypeMessage<>(KafkaMessageType.ORDER_SINGLE_PRODUCT,
                new KafkaDto.ProductUpdateStock(productId, qty)));

        // TODO: 쿠폰 상태 수정 Kafka Listener 토픽 수정
        kafkaProducer.send("kokodo.coupon.status", new KafkaDto.CouponUpdateStatus(couponId));

        return order.getId();
    }

    @Transactional
    public Long orderCartProducts(Long memberId, List<Long> cartIds, List<Long> couponIds) {
        // '장바구니상품' 조회
        List<Cart> carts = cartRepository.findByIdIn(cartIds);

        // 주문 상품 생성
        List<OrderProduct> orderProducts = carts.stream()
            .map(cart -> modelMapper.map(cart, OrderProduct.class))
            .collect(Collectors.toList());

        // 단일상품가격(unitPrice) 세팅
        List<Long> productIds = carts.stream()
            .map(Cart::getProductId)
            .collect(Collectors.toList());

        List<FeignResponse.ProductPrice> productPrices = productServiceClient.getProducts(productIds);
        modelMapper.map(productPrices, orderProducts);

        // 주문 총 가격 계산
        Integer totalPrice = orderProducts.stream()
            .map(OrderProduct::getUnitPrice)
            .filter(Objects::nonNull)
            .mapToInt(Integer::intValue)
            .sum();

        // 사용자 이름, 주소
//        MemberResponse memberResponse = memberServiceClient.getMember(memberId);
        MemberResponse memberResponse = new MemberResponse("NaYeon Kwon",
            "서울특별시 강남구 가로수길 43");

        // 주문 생성
        Order order = Order.builder()
            .deliveryMemberName(memberResponse.getMemberName())
            .deliveryMemberAddress(memberResponse.getMemberAddress())
            .totalPrice(totalPrice)
            .orderDate(LocalDateTime.now())
            .orderProducts(orderProducts)
            .build();

        orderRepository.save(order);

        // 상품 재고 감소
        // productId (key) - qty (value) Map 생성
        Map<Long,Integer> productIdQtyMap = carts.stream()
            .collect(Collectors.toMap(Cart::getProductId, Cart::getQty));

        kafkaProducer.send("kokodo.product.de-stock",
            new KafkaDto.ProductUpdateStockTypeMessage<>(KafkaMessageType.ORDER_CART_PRODUCT,
                productIdQtyMap));

        // [key] "couponIds"    [value] Long List
        // map.get("couponIds")
        kafkaProducer.send("kokodo.coupon.status", new KafkaDto.CouponUpdateStatusList(couponIds));

        return order.getId();
    }
}
