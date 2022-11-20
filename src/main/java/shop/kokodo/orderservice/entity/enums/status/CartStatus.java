package shop.kokodo.orderservice.entity.enums.status;

import shop.kokodo.orderservice.entity.enums.EnumType;

public enum CartStatus implements EnumType {

    IN_CART("장바구니 상품"),
    ORDER_PROCESS("주문 처리"),
    DELETED("삭제된 장바구니");

    private String value;

    CartStatus(String value){
        this.value = value;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }
}
