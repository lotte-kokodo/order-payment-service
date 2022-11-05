package shop.kokodo.orderpaymentservice.service.interfaces;

import java.util.List;
import java.util.Map;
import shop.kokodo.orderpaymentservice.dto.response.dto.CartAvailableDto;
import shop.kokodo.orderpaymentservice.dto.response.dto.CartDto;
import shop.kokodo.orderpaymentservice.dto.response.dto.CartQtyDto;
import shop.kokodo.orderpaymentservice.entity.Cart;

public interface CartService {

    /* 장바구니 상품 생성 (장바구니 담기 기능) */
    Cart createCart(Long memberId, Long productId, Integer qty);

    /* 장바구니 상품 조회 */
    Map<Long, List<CartDto>> getCarts(Long memberId);

    /* 장바구니 상품 수량 업데이트 API */
    CartAvailableDto updateQty(CartQtyDto req);
}
