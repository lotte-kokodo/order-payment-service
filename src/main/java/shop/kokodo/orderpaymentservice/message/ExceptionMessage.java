package shop.kokodo.orderpaymentservice.message;

public class ExceptionMessage {

    public static final String OUT_OF_STOCK = "최대 주문 가능한 개수";
    public static final String CART_NOT_FOUNDED = "유효하지 않은 장바구니 아이디";
    public static final String CART_QTY_CANNOT_BE_NEGATIVE = "유효하지 않은 상품 수량 (수량 < 0)";

    /* 상품 재고 부족 메시지 생성 */
    // msg: 상품 재고 부족: product_id '상품아이디'
    public static String createProductOutOfStockMsg(Integer availableQty) {
        return String.format(OUT_OF_STOCK + ": %d개", availableQty);
    }

    /* 유효하지 않은 장바구니 메시지 생성 */
    public static String createCartNotFoundMsg(Long cartId) {
        return String.format(CART_NOT_FOUNDED + ": cart_id '%d'", cartId);
    }
}
