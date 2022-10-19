package shop.kokodo.orderpaymentservice.entity.enums.status;

import shop.kokodo.orderpaymentservice.entity.enums.EnumType;

public enum CartStatus implements EnumType {

    IN_CART("장바구니 상품"),
    ORDER_PROCESS("주문 처리");

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
