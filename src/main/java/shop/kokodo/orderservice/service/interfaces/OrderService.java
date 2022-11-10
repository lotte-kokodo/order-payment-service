package shop.kokodo.orderservice.service.interfaces;

import shop.kokodo.orderservice.dto.request.CartOrderRequest;
import shop.kokodo.orderservice.dto.request.SingleProductOrderRequest;
import shop.kokodo.orderservice.dto.response.data.OrderResponse.GetOrderProduct;
import shop.kokodo.orderservice.dto.response.dto.OrderDetailInformationDto;
import shop.kokodo.orderservice.dto.response.dto.OrderInformationDto;
import shop.kokodo.orderservice.entity.Order;

import java.util.List;
import java.util.Map;


public interface OrderService {

    /* 개별 상품 주문 */
    Order orderSingleProduct(SingleProductOrderRequest dto);

    /* 장바구니 상품 주문 */
    Order orderCartProducts(CartOrderRequest dto);

    /* 주문서 조회 */
    Map<Long, GetOrderProduct> getOrderSheetProducts(Long memberId, List<Long> productIds);

    /* 주문 조회 */
    List<OrderInformationDto> getOrderList(Long memberId);

    /* 주문 상세 조회 */
    List<OrderDetailInformationDto> getOrderDetailList(Long memberId, Long orderId);

}
