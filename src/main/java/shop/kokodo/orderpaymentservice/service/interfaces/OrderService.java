package shop.kokodo.orderpaymentservice.service.interfaces;

import shop.kokodo.orderpaymentservice.dto.response.data.OrderResponse.GetOrderProduct;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderDetailInformationDto;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderInformationDto;
import shop.kokodo.orderpaymentservice.entity.Order;

import java.util.List;
import java.util.Map;


public interface OrderService {

    /* 개별 상품 주문 */
    Order orderSingleProduct(Long memberId, Long productId, Long sellerId, Integer qty,
                                Long rateCouponId, Long fixCouponId);

    /* 장바구니 상품 주문 */
    Order orderCartProducts(Long memberId, List<Long> cartIds, Map<Long, Long> productSellerMap,
                             List<Long> rateCouponIds, List<Long> fixCouponIds);

    /* 주문서 조회 */
    Map<Long, GetOrderProduct> getOrderSheetProducts(Long memberId, List<Long> productIds);

    /* 주문 조회 */
    List<OrderInformationDto> getOrderList(Long memberId);

    /* 주문 상세 조회 */
    List<OrderDetailInformationDto> getOrderDetailList(Long memberId, Long orderId);

    Map<Long, List<Integer>> getProductAllPrice(List<Long> productIdList);
}
