package shop.kokodo.orderservice.entity.enums.status;

import shop.kokodo.orderservice.entity.enums.EnumType;

public enum PaymentMethod implements EnumType {

    CARD("카드"), // 카드
    DEPOSIT_WITHOUT_BANKBOOK("무통장입금"),
    BANK_TRANSFER("계좌이체");

    private String value;

    PaymentMethod(String value) { this.value = value; }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }
}
