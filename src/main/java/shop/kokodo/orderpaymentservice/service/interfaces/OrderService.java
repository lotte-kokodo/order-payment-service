package shop.kokodo.orderpaymentservice.service.interfaces;


import shop.kokodo.orderpaymentservice.dto.response.dto.OrderDetailInformationDto;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderInformationDto;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderResponse;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import shop.kokodo.orderpaymentservice.dto.request.OrderRequest;
import shop.kokodo.orderpaymentservice.dto.request.OrderRequest.GetOrderSheet;
import shop.kokodo.orderpaymentservice.dto.response.data.OrderResponse;
import shop.kokodo.orderpaymentservice.dto.response.data.OrderResponse.OrderSheet;
import shop.kokodo.orderpaymentservice.entity.Order;

public interface OrderService {

    /* 개별 상품 주문 */
    Order orderSingleProduct(Long memberId, Long productId, Integer qty, Long couponId);

    /* 장바구니 상품 주문 */
    Order orderCartProducts(Long memberId, List<Long> cartIds, List<Long> couponIds);

    /* 주문서 조회 */
    OrderSheet getOrderSheet(Long memberId, List<Long> productIds);
    /* 주문 조회 */
    List<OrderInformationDto> getOrderList(Long memberId);

    /* 주문 상세 조회 */
    List<OrderDetailInformationDto> getOrderDetailList(Long memberId, Long orderId);
}
