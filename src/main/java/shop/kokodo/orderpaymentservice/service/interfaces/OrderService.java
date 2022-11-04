package shop.kokodo.orderpaymentservice.service.interfaces;

import shop.kokodo.orderpaymentservice.dto.request.order.CartOrderDto;
import shop.kokodo.orderpaymentservice.dto.request.order.SingleProductOrderDto;
import shop.kokodo.orderpaymentservice.dto.response.data.OrderResponse.GetOrderProduct;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderDetailInformationDto;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderInformationDto;
import shop.kokodo.orderpaymentservice.entity.Order;

import java.util.List;
import java.util.Map;
import shop.kokodo.orderpaymentservice.dto.request.OrderRequest.CouponProductPrice;
import shop.kokodo.orderpaymentservice.dto.response.data.OrderResponse.GetOrderProduct;
import shop.kokodo.orderpaymentservice.entity.Order;


public interface OrderService {

    /* 개별 상품 주문 */
    Order orderSingleProduct(SingleProductOrderDto dto);

    /* 장바구니 상품 주문 */
    Order orderCartProducts(CartOrderDto dto);

    /* 주문서 조회 */
    Map<Long, GetOrderProduct> getOrderSheetProducts(Long memberId, List<Long> productIds);

    /* 주문 조회 */
    List<OrderInformationDto> getOrderList(Long memberId);

    /* 주문 상세 조회 */
    List<OrderDetailInformationDto> getOrderDetailList(Long memberId, Long orderId);

}
