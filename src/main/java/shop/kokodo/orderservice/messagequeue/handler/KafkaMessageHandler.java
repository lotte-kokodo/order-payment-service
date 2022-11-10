package shop.kokodo.orderservice.messagequeue.handler;

public interface KafkaMessageHandler {

    void handle(String message);

}
