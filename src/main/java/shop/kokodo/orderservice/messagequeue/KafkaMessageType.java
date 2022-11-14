package shop.kokodo.orderservice.messagequeue;

public enum KafkaMessageType {

    ORDER_SINGLE_PRODUCT("ORDER SINGLE PRODUCT"),
    ORDER_CART_PRODUCT("ORDER CART PRODUCT");

    private final String key;

    KafkaMessageType(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

}
