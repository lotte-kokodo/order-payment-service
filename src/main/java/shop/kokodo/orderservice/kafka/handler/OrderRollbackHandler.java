package shop.kokodo.orderservice.kafka.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.kokodo.orderservice.entity.Order;
import shop.kokodo.orderservice.entity.enums.status.OrderStatus;
import shop.kokodo.orderservice.kafka.KafkaMessageParser;
import shop.kokodo.orderservice.kafka.dto.KafkaOrderDto;
import shop.kokodo.orderservice.repository.interfaces.OrderRepository;

@Component
@Slf4j
public class OrderRollbackHandler implements KafkaMessageHandler {
    private final OrderRepository orderRepository;

    private final KafkaMessageParser parser;

    @Autowired
    public OrderRollbackHandler(
        OrderRepository orderRepository,
        KafkaMessageParser parser) {
        this.orderRepository = orderRepository;
        this.parser = parser;
    }

    @Override
    public void handle(String message) {
        KafkaOrderDto kafkaOrder = parser.readMessageValue(message, new TypeReference<KafkaOrderDto>() {});

        Long orderId = kafkaOrder.getOrderId();
        Order order = orderRepository.findById(orderId).orElseThrow(
            () -> {
                log.error("유효하지 않은 주문 아이디: {}", orderId);
                throw new IllegalArgumentException("유효하지 않은 주문 아이디 " + orderId);
            }
        );

        order.changeOrderState(OrderStatus.ORDER_ERROR);
        orderRepository.save(order);
    }
}
