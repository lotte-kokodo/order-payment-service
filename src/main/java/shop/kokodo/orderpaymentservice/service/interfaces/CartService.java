package shop.kokodo.orderpaymentservice.service.interfaces;

import org.springframework.stereotype.Repository;
import shop.kokodo.orderpaymentservice.entity.Cart;

@Repository
public interface CartService {

    /* 장바구니 상품 생성 */
    Long createCart(Cart cart);
}
