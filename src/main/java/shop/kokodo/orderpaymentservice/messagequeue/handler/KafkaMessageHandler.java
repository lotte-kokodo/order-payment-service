package shop.kokodo.orderpaymentservice.messagequeue.handler;

public interface KafkaMessageHandler {

    void handle(String message);

}
