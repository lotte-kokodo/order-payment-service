package shop.kokodo.orderpaymentservice.entity.enums.payment;

import shop.kokodo.orderpaymentservice.entity.enums.EnumType;

public enum PaymentStatus implements EnumType {

    APPROVAL("결제 승인"),
    DENIAL("결제 거부");

    private String value;

    PaymentStatus(String value) { this.value = value; }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }
}
