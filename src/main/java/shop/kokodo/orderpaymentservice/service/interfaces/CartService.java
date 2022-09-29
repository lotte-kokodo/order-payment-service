package shop.kokodo.orderpaymentservice.service.interfaces;

public interface CartService {

    /* 장바구니 상품 생성 (장바구니 담기 기능) */
    Long createCart(Long memberId, Long productId, Integer qty);

}
