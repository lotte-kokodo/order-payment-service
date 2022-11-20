package shop.kokodo.orderservice.service.interfaces;

import java.util.List;
import java.util.Map;
import shop.kokodo.orderservice.dto.request.CartRequestDto;
import shop.kokodo.orderservice.dto.response.CartAvailableQtyDto;
import shop.kokodo.orderservice.dto.request.CartQtyDto;
import shop.kokodo.orderservice.dto.response.CartResponseDto;
import shop.kokodo.orderservice.entity.Cart;

public interface CartService {

    /* 장바구니 상품 생성 (장바구니 담기 기능) */
    Cart createCart(CartRequestDto req);

    /* 장바구니 상품 조회 */
    Map<Long, List<CartResponseDto>> getCarts(Long memberId);

    /* 장바구니 상품 조회 */
    String deleteCarts(List<Long> cartIds);

    /* 장바구니 상품 수량 업데이트 */
    CartAvailableQtyDto updateQty(CartQtyDto req);
}
