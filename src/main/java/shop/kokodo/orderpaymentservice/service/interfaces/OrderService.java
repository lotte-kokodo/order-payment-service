package shop.kokodo.orderpaymentservice.service.interfaces;


import java.util.List;
import shop.kokodo.orderpaymentservice.entity.Order;

public interface OrderService {

    /* 개별 상품 주문 */
    Order orderSingleProduct(Long memberId, Long productId, Integer qty, Long couponId);

    /* 장바구니 상품 주문 */
    Order orderCartProducts(Long memberId, List<Long> cartIds, List<Long> couponIds);

}
