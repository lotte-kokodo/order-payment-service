package shop.kokodo.orderpaymentservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.kokodo.orderpaymentservice.dto.response.dto.MemberResponse;
import shop.kokodo.orderpaymentservice.dto.response.dto.ProductResponse;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;
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
    private final MemberServiceClient memberServiceClient;
    private final ProductServiceClient productServiceClient;

    @Autowired
    public OrderServiceImpl(
        ModelMapper modelMapper,
        OrderRepository orderRepository,
        CartRepository cartRepository,
        MemberServiceClient memberServiceClient,
        ProductServiceClient productServiceClient) {

        this.modelMapper = modelMapper;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.memberServiceClient = memberServiceClient;
        this.productServiceClient = productServiceClient;
    }

    @Transactional
    public Long orderSingleProduct(Long memberId, Long productId, Integer qty) {

        // TODO: FeignClient 통신 테스트
        // 상품 가격
        ProductResponse productResponse = productServiceClient.getProduct(productId);
        Integer unitPrice = productResponse.getUnitPrice();

        // 주문 상품 생성
        OrderProduct orderProduct = OrderProduct.builder()
            .memberId(memberId)
            .productId(productId)
            .qty(qty)
            .unitPrice(unitPrice)
            .build();


        // TODO: FeignClient 통신 테스트
        // 사용자 이름, 주소
        MemberResponse memberResponse = memberServiceClient.getMember(memberId);

        // 주문 생성
        Order order = Order.builder()
            .deliveryMemberName(memberResponse.getMemberName())
            .deliveryMemberAddress(memberResponse.getMemberAddress())
            .totalPrice(unitPrice*qty)
            .orderDate(LocalDateTime.now())
            .orderProducts(List.of(orderProduct))
            .build();

        orderRepository.save(order);

        return order.getId();
    }

    @Transactional
    public Long orderCartProducts(Long memberId, List<Long> cartIds) {
        // '장바구니상품' 조회
        List<Cart> carts = cartRepository.findByIdIn(cartIds);

        // 주문 상품 생성
        List<OrderProduct> orderProducts = carts.stream()
            .map(cart -> modelMapper.map(cart, OrderProduct.class))
            .collect(Collectors.toList());

        // 단일상품가격(unitPrice) 세팅
        List<Long> productIds = carts.stream()
            .map(Cart::getProductId)
            .collect(Collectors.toList());;

        List<ProductResponse> productResponses = productServiceClient.getProducts(productIds);
        modelMapper.map(productResponses, orderProducts);

        // 주문 총 가격 계산
        Integer totalPrice = orderProducts.stream()
            .mapToInt(OrderProduct::getUnitPrice)
            .sum();

        // 사용자 이름, 주소
        MemberResponse memberResponse = memberServiceClient.getMember(memberId);

        // 주문 생성
        Order order = Order.builder()
            .deliveryMemberName(memberResponse.getMemberName())
            .deliveryMemberAddress(memberResponse.getMemberAddress())
            .totalPrice(totalPrice)
            .orderDate(LocalDateTime.now())
            .orderProducts(orderProducts)
            .build();

        orderRepository.save(order);

        return order.getId();
    }
}
