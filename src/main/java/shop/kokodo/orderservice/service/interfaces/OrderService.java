package shop.kokodo.orderservice.service.interfaces;

import shop.kokodo.orderservice.dto.request.CartOrderDto;
import shop.kokodo.orderservice.dto.request.SingleProductOrderDto;
import shop.kokodo.orderservice.dto.response.OrderDetailInformationDto;
import shop.kokodo.orderservice.dto.response.OrderInformationDto;
import shop.kokodo.orderservice.entity.Order;

import java.util.List;


public interface OrderService {

    /* 개별 상품 주문 */
    Order orderSingleProduct(SingleProductOrderDto dto);

    /* 장바구니 상품 주문 */
    Order orderCartProducts(CartOrderDto dto);

    /* 주문 조회 */
    List<OrderInformationDto> getOrderList(Long memberId);

    /* 주문 상세 조회 */
    List<OrderDetailInformationDto> getOrderDetailList(Long memberId, Long orderId);

}
