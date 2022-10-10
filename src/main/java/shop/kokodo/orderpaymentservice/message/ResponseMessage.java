package shop.kokodo.orderpaymentservice.message;

public class ResponseMessage {

    public static final String CREATE_ORDER_SUCCESS = "주문 성공";
    public static final String CREATE_ORDER_FAILURE = "주문 실패";

    public static final String CREATE_CART_SUCCESS = "장바구니상품 추가 성공";
    public static final String CREATE_CART_FAILURE = "장바구니상품 추가 실패";

    public static final String GET_CART_SUCCESS = "장바구니 조회 성공";

    public static final String INCREASE_CART_QTY_SUCCESS = "장바구니 상품 수량 증가 성공";
    public static final String DECREASE_CART_QTY_SUCCESS = "장바구니 상품 수량 감소 성공";
}
