package shop.kokodo.orderpaymentservice.message;

public interface ResponseMessage {

    public static final String CREATE_ORDER_SUCCESS = "주문 완료";
    public static final String CREATE_ORDER_FAILURE = "주문 실패";

    public static final String CREATE_CART_SUCCESS = "장바구니 상품 추가 완료";
    public static final String CREATE_CART_FAILURE = "장바구니 상품 추가 실패";

}
