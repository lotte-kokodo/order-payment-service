package shop.kokodo.orderpaymentservice.service.interfaces;

import shop.kokodo.orderpaymentservice.entity.Cart;

public interface CartService {

    /* 장바구니 상품 생성 (장바구니 담기 기능) */
    Cart createCart(Long memberId, Long productId, Integer qty);

}
