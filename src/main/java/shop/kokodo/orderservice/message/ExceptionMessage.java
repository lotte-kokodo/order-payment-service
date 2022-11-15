package shop.kokodo.orderservice.message;

public class ExceptionMessage {

    public static final String OUT_OF_STOCK = "최대 주문 가능한 개수";
    public static final String CART_NOT_FOUNDED = "유효하지 않은 장바구니 아이디";
    public static final String CART_QTY_CANNOT_BE_NEGATIVE = "유효하지 않은 상품 수량 (수량 < 0)";
    public static final String CANNOT_BE_ATTEMPTED_COMMUNICATION = "잠시 후 다시 시도해주시거나, 관리자에게 문의하세요 🥹";
    public static final String NOT_REGISTERED_MEMBER_INFO = "배송정보 미등록";
    /* 상품 재고 부족 메시지 생성 */
    // msg: 상품 재고 부족: product_id '상품아이디'
    public static String createProductOutOfStockMsg(Integer availableQty) {
        return String.format(OUT_OF_STOCK + ": %d개", availableQty);
    }

    /* 유효하지 않은 장바구니 메시지 생성 */
    public static String createCartNotFoundMsg(Long cartId) {
        return String.format(CART_NOT_FOUNDED + ": cart_id '%d'", cartId);
    }

    /**
     * 마이크로서비스 통신 오류 메시지 생성
     */
}
