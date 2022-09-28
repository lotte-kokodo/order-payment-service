package shop.kokodo.orderpaymentservice.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderRepository;
import shop.kokodo.orderpaymentservice.service.interfaces.OrderService;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Long order(Order order, List<Long> cartIds) {
        orderRepository.save(order);

        /* 장바구니 상품에 주문 아이디 설정 */
        List<Cart> carts = cartRepository.findByIdIn(cartIds);
        carts.forEach(c -> c.setOrder(order));

        return order.getId();
    }
}
