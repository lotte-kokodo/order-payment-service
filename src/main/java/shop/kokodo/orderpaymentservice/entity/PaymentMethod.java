package shop.kokodo.orderpaymentservice.entity;

public enum PaymentMethod {

    CARD, // 카드 결제
    DEPOSIT_WITHOUT_BANKBOOK, // 무통장입금 결제
    BANK_TRANSFER // 계좌이체 결제

}
