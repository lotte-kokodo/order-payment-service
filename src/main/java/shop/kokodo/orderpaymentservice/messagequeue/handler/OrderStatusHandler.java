package shop.kokodo.orderpaymentservice.messagequeue.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.enums.status.OrderStatus;
import shop.kokodo.orderpaymentservice.messagequeue.KafkaMessageParser;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderRepository;

import java.util.Optional;

/**
 * '주문취소' 시 상품 재고 증가
 */
@Component
@Slf4j
public class OrderStatusHandler implements KafkaMessageHandler {

    private final ObjectMapper objectMapper;

    private final OrderRepository orderRepository;

    private final KafkaMessageParser parser;

    @Autowired
    public OrderStatusHandler(
        ObjectMapper objectMapper,
        OrderRepository orderRepository,
        KafkaMessageParser parser) {
        this.objectMapper = objectMapper;
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
        findOrder.setOrderStatus(OrderStatus.PURCHASE_CONFIRM);
        orderRepository.save(findOrder);
    }


}
