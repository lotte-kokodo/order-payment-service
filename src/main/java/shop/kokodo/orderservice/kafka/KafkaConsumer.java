package shop.kokodo.orderservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import shop.kokodo.orderservice.kafka.handler.OrderRollbackHandler;
import shop.kokodo.orderservice.kafka.handler.OrderStatusHandler;

@Service
@Slf4j
public class KafkaConsumer {

    private final OrderStatusHandler orderStatusHandler;
    private final OrderRollbackHandler orderRollbackHandler;

    public KafkaConsumer(OrderStatusHandler orderStatusHandler,
        OrderRollbackHandler orderRollbackHandler) {
        this.orderStatusHandler = orderStatusHandler;
        this.orderRollbackHandler = orderRollbackHandler;
    }


    @KafkaListener(topics = "order-id-topic")
    public void decreaseStock(String message) {
        log.info("[KafkaConsumer] consume message: {}", message);

        orderStatusHandler.handle(message);
    }

    @KafkaListener(topics = "order-rollback")
    public void rollbackOrder(String message) {
        log.info("[KafkaConsumer] consume message: {}", message);

        orderRollbackHandler.handle(message);
    }
}
