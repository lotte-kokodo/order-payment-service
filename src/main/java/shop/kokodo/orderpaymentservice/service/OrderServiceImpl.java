package shop.kokodo.orderpaymentservice.service;

import java.time.LocalDateTime;
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
import shop.kokodo.orderpaymentservice.entity.enums.order.OrderStatus;
import shop.kokodo.orderpaymentservice.feign.client.MemberServiceClient;
import shop.kokodo.orderpaymentservice.feign.client.ProductServiceClient;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.MemberAddress;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.MemberOfOrderSheet;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductOfOrderSheet;
import shop.kokodo.orderpaymentservice.messagequeue.KafkaProducer;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderRepository;
import shop.kokodo.orderpaymentservice.service.interfaces.OrderService;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final ModelMapper modelMapper;

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    // FeignClient
    private final ProductServiceClient productServiceClient;
    private final MemberServiceClient memberServiceClient;

    private final KafkaProducer kafkaProducer;

    @Autowired
    public OrderServiceImpl(
        ModelMapper modelMapper,
        OrderRepository orderRepository,
        CartRepository cartRepository,
        ProductServiceClient productServiceClient,
        MemberServiceClient memberServiceClient,
        KafkaProducer kafkaProducer) {

        this.modelMapper = modelMapper;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productServiceClient = productServiceClient;
        this.memberServiceClient = memberServiceClient;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    public Order orderSingleProduct(Long memberId, Long productId, Integer qty, Long couponId) {

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
        MemberAddress memberAddress = memberServiceClient.getMemberAddress(memberId);
//        MemberResponse memberResponse = new MemberResponse("NaYeon Kwon",
//            "서울특별시 강남구 가로수길 43");

        // 주문 생성
        Order order = Order.builder()
            .memberId(memberId)
            .deliveryMemberAddress(memberAddress.getAddress())
            .totalPrice(unitPrice*qty)
            .orderDate(LocalDateTime.now())
            .orderProducts(List.of(orderProduct))
            .orderStatus(OrderStatus.ORDER_SUCCESS)
            .build();

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
        MemberAddress memberAddress = memberServiceClient.getMemberAddress(memberId);
//        FeignResponse.MemberDeliveryInfo memberDeliveryInfo
//            = new FeignResponse.MemberDeliveryInfo("NaYeon Kwon", "서울특별시 강남구 가로수길 43");

        // 주문 생성
        Order order = Order.builder()
            .deliveryMemberAddress(memberAddress.getAddress())
            .totalPrice(totalPrice)
            .orderDate(LocalDateTime.now())
            .orderProducts(orderProducts)
            .orderStatus(OrderStatus.ORDER_SUCCESS)
            .build();

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
