package shop.kokodo.orderservice.entity.enums.status;

import shop.kokodo.orderservice.entity.enums.EnumType;

public enum OrderStatus implements EnumType {

    ORDER_SUCCESS("주문/결제 완료"),
    PURCHASE_CONFIRM("구매 확정"),
    ORDER_ERROR("주문 오류");

    private String value;

    OrderStatus(String value){
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
