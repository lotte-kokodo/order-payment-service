package shop.kokodo.orderservice.controller;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.kokodo.orderservice.dto.request.CartOrderRequest;
import shop.kokodo.orderservice.dto.request.SingleProductOrderRequest;
import shop.kokodo.orderservice.dto.response.Response;
import shop.kokodo.orderservice.dto.response.data.OrderResponse.GetOrderProduct;
import shop.kokodo.orderservice.dto.response.data.ResultMessage;
import shop.kokodo.orderservice.dto.response.dto.OrderDetailInformationDto;
import shop.kokodo.orderservice.dto.response.dto.OrderInformationDto;
import shop.kokodo.orderservice.entity.Order;
import shop.kokodo.orderservice.message.MessageFormat;
import shop.kokodo.orderservice.service.interfaces.OrderService;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /* 단일 상품 주문 API */
    @PostMapping("/singleProduct")
    public Response orderSingleProduct(@Valid @RequestBody SingleProductOrderRequest req) {
        Order order = orderService.orderSingleProduct(req);
        return Response.success(new ResultMessage(order.getId(), MessageFormat.CREATE_ORDER_SUCCESS));
    }

    /* 장바구니 주문 API */
    @PostMapping("/cart")
    public Response orderCartProduct(@Valid @RequestBody CartOrderRequest req) {
        Order order = orderService.orderCartProducts(req);
        return Response.success(new ResultMessage(order.getId(), MessageFormat.CREATE_ORDER_SUCCESS));
    }

    /**
     * 주문 내역 조회
     * @param memberId
     * @return 주문 정보 리스트
     */
    @GetMapping("/{memberId}")
    public Response orderList(@PathVariable("memberId") Long memberId) {
        List<OrderInformationDto> orderInformationDto = orderService.getOrderList(memberId);
        return Response.success(orderInformationDto);
    }

    /**
     * 주문 내역 상세 조회 API
     * @param memberId
     * @param orderId
     * @return 주문 상세 정보 리스트
     */
    @GetMapping("/{memberId}/{orderId}")
    public List<OrderDetailInformationDto> orderDetailList(@PathVariable("memberId")Long memberId, @PathVariable("orderId")Long orderId) {
        List<OrderDetailInformationDto> orderDetailInformationDtoList = orderService.getOrderDetailList(memberId, orderId);
        return orderDetailInformationDtoList;
    }

    /* 주문서 조회 API */
    @GetMapping("/{memberId}/orderSheet")
    public Response getOrderSheet(@PathVariable Long memberId, @RequestParam List<Long> productIds) {
        Map<Long, GetOrderProduct> orderProducts = orderService.getOrderSheetProducts(memberId, productIds);
        return Response.success(orderProducts);
    }
}
