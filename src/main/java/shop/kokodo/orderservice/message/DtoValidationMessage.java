package shop.kokodo.orderservice.message;

public class DtoValidationMessage {

    public static final String MEMBER_ID_NULL = "회원 아이디 NULL";

    public static final String CART_IDS_NULL = "장바구니 아이디 리스트 NULL";

    // 쿠폰 아이디 리스트는 적용된 쿠폰이 없더라도 빈 배열을 요청데이터로 보내야 한다.
    public static final String RATE_COUPON_IDS_NULL = "비율할인쿠폰 아이디 리스트 NULL";
    public static final String FIX_COUPON_IDS_NULL = "비율할인쿠폰 아이디 리스트 NULL";

    public static final String PRODUCT_ID_NULL = "상품 아이디 NULL";
    public static final String SELLER_ID_BLANK = "판매자 아이디 BLANK";
    public static final String QTY_NULL = "상품수량 BLANK";

    public static final String CART_ID_NULL = "장바구니 아이디 BLANK";
}
