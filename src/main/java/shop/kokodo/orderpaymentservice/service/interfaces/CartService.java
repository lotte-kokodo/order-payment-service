package shop.kokodo.orderpaymentservice.service.interfaces;

import java.util.List;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import shop.kokodo.orderpaymentservice.dto.response.Response;
import shop.kokodo.orderpaymentservice.dto.response.data.CartResponse;
import shop.kokodo.orderpaymentservice.dto.response.data.CartResponse.GetCart;
import shop.kokodo.orderpaymentservice.dto.response.data.ResultMessage;
import shop.kokodo.orderpaymentservice.entity.Cart;

public interface CartService {

    /* 장바구니 상품 생성 (장바구니 담기 기능) */
    Cart createCart(Long memberId, Long productId, Integer qty);

    /* 장바구니 상품 조회 */
    List<GetCart> getCartProducts(Long memberId);

    /* 장바구니 상품 수량 업데이트 API */
    CartResponse.UpdateCartQty updateQty(Long cartId, Integer updatedQty);
}
