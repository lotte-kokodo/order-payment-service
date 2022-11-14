package shop.kokodo.orderservice.messagequeue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import shop.kokodo.orderservice.messagequeue.handler.OrderStatusHandler;

@Service
@Slf4j
public class KafkaConsumer {

    private final OrderStatusHandler orderStatusHandler;

    public KafkaConsumer(OrderStatusHandler orderStatusHandler) {
        this.orderStatusHandler = orderStatusHandler;
    }


    @KafkaListener(topics = "order-id-topic")
    public void decreaseStock(String message) {
        log.info("[KafkaConsumer] consume message: {}", message);

        orderStatusHandler.handle(message);
    }
}
