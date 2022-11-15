package shop.kokodo.orderservice.kafka.handler;

public interface KafkaMessageHandler {

    void handle(String message);

}
