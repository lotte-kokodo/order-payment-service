package shop.kokodo.orderservice.service.interfaces;

import java.util.List;
import java.util.Map;
import shop.kokodo.orderservice.dto.request.CartRequest;
import shop.kokodo.orderservice.dto.response.dto.CartAvailableQtyResponse;
import shop.kokodo.orderservice.dto.response.dto.CartResponse;
import shop.kokodo.orderservice.dto.response.dto.CartQtyRequest;
import shop.kokodo.orderservice.entity.Cart;

public interface CartService {

    /* 장바구니 상품 생성 (장바구니 담기 기능) */
    Cart createCart(CartRequest req);

    /* 장바구니 상품 조회 */
    Map<Long, List<CartResponse>> getCarts(Long memberId);

    /* 장바구니 상품 수량 업데이트 API */
    CartAvailableQtyResponse updateQty(CartQtyRequest req);
}
