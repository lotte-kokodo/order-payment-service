package shop.kokodo.orderpaymentservice.service.interfaces;


import shop.kokodo.orderpaymentservice.dto.response.dto.OrderInformationDto;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderResponse;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;

import java.util.List;

public interface OrderService {

    /* 개별 상품 주문 */
    Long orderSingleProduct(Long memberId, Long productId, Integer qty);

    /* 장바구니 상품 주문 */
    Long orderCartProducts(Long memberId, List<Long> cartIds);

    /* 주문 조회 */
    List<OrderInformationDto> getOrderList(Long memberId);

    /* 주문 상세 조회 */
    List<OrderResponse> getOrderDetailList(Long memberId);
}
