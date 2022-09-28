package shop.kokodo.orderpaymentservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    public OrderServiceImpl(
        ModelMapper modelMapper,
        OrderRepository orderRepository,
        CartRepository cartRepository) {

        this.modelMapper = modelMapper;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Long orderSingleProduct(Long memberId, Long productId, Integer qty) {

        // TODO: 사용자 데이터 조회 API
        // 사용자 이름, 주소
        String memberName = null;
        String memberAddress = null;


        // TODO: 상품 데이터 조회 API
        // 상품 가격
        Integer unitPrice = 1;


        // 주문 상품 생성
        OrderProduct orderProduct = OrderProduct.builder()
            .memberId(memberId)
            .productId(productId)
            .qty(qty)
            .unitPrice(unitPrice)
            .build();

        // 주문 생성
        Order order = Order.builder()
            .deliveryMemberName(memberName)
            .deliveryMemberAddress(memberAddress)
            .totalPrice(unitPrice*qty)
            .orderDate(LocalDateTime.now())
            .orderProducts(List.of(orderProduct))
            .build();

        orderRepository.save(order);

        return order.getId();
    }

    @Transactional
    public Long orderCartProducts(Long memberId, List<Long> cartIds) {
        // TODO: 사용자 데이터 조회 API
        // 사용자 이름, 주소
        String memberName = null;
        String memberAddress = null;

        // TODO: 상품 데이터 조회 API
        // '장바구니상품' 으로부터 '주문상품' 생성
        List<Cart> carts = cartRepository.findByIdIn(cartIds);
        List<Long> productIds = carts.stream()
            .map(Cart::getProductId)
            .collect(Collectors.toList());;

        // Feign Client - List<Product>

        // 주문 상품 생성
        List<OrderProduct> orderProducts = carts.stream()
            .map(cart -> modelMapper.map(cart, OrderProduct.class))
            .collect(Collectors.toList());

        // TODO: 단일상품가격(unitPrice) 세팅

        Integer totalPrice = orderProducts.stream()
            .mapToInt(OrderProduct::getUnitPrice)
            .sum();

        // 주문 생성
        Order order = Order.builder()
            .deliveryMemberName(memberName)
            .deliveryMemberAddress(memberAddress)
            .totalPrice(totalPrice)
            .orderDate(LocalDateTime.now())
            .orderProducts(orderProducts)
            .build();

        orderRepository.save(order);

        return order.getId();
    }
}
