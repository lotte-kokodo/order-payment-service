package shop.kokodo.orderpaymentservice.service.interfaces;


import java.util.List;
import shop.kokodo.orderpaymentservice.dto.response.data.OrderResponse.GetOrderProduct;
import shop.kokodo.orderpaymentservice.entity.Order;

public interface OrderService {

    /* 개별 상품 주문 */
    Order orderSingleProduct(Long memberId, Long productId, Integer qty, Long couponId);

    /* 장바구니 상품 주문 */
    Order orderCartProducts(Long memberId, List<Long> cartIds, List<Long> couponIds);

    /* 주문서 조회 */
    List<GetOrderProduct> getOrderSheetProducts(Long memberId, List<Long> productIds);
}
