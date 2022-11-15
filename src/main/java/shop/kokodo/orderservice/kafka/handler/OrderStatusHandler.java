package shop.kokodo.orderservice.kafka.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.kokodo.orderservice.entity.Order;
import shop.kokodo.orderservice.entity.enums.status.OrderStatus;
import shop.kokodo.orderservice.kafka.KafkaMessageParser;
import shop.kokodo.orderservice.repository.interfaces.OrderRepository;

/**
 * '주문취소' 시 상품 재고 증가
 */
@Component
@Slf4j
public class OrderStatusHandler implements KafkaMessageHandler {

    private final OrderRepository orderRepository;

    private final KafkaMessageParser parser;

    @Autowired
    public OrderStatusHandler(
        OrderRepository orderRepository,
        KafkaMessageParser parser) {
        this.orderRepository = orderRepository;
        this.parser = parser;
    }

    @Override
    public void handle(String message) {
        Long orderId = parser.readMessageValue(message, new TypeReference<Long>() {});
        log.info(message);
        // TODO: NULL 처리
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            log.error("유효하지 않은 주문 아이디: {}", orderId);
            throw new IllegalArgumentException("유효하지 않은 주문 아이디 " + orderId);
        }

        Order findOrder = order.get();
        findOrder.changeOrderState(OrderStatus.PURCHASE_CONFIRM);
        orderRepository.save(findOrder);
    }


}
