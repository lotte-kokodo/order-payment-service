package shop.kokodo.orderpaymentservice.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.kokodo.orderpaymentservice.dto.request.OrderRequest;
import shop.kokodo.orderpaymentservice.dto.response.Response;
import shop.kokodo.orderpaymentservice.dto.response.dto.IdAndMessageDto;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.message.ResponseMessage;
import shop.kokodo.orderpaymentservice.service.interfaces.OrderService;

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
    @PostMapping("/{memberId}/single-product")
    public Response orderSingleProduct(@PathVariable("memberId") Long memberId,
                                        @RequestParam("productId") Long productId,
                                        @RequestParam("qty") Integer qty) {

        Long orderId = orderService.orderSingleProduct(memberId, productId, qty);

        return Response.success(new IdAndMessageDto.CreateSuccess(orderId,
            ResponseMessage.CREATE_ORDER_SUCCESS));

    }

    /* 장바구니 주문 API */
    // TODO 사용자 정보 어디서 넘어오는지 확인
    @PostMapping("/{memberId}/cart")
    public Response orderCartProduct(@PathVariable("memberId") Long memberId,
                                    @RequestBody OrderRequest.CreateCartOrder req) {

        List<Long> cartIds = req.getCartIds();

        Long orderId = orderService.orderCartProducts(memberId, cartIds);

        return Response.success(
            new IdAndMessageDto.CreateSuccess(orderId, ResponseMessage.CREATE_ORDER_SUCCESS));
    }

    /* 주문 내역 조회 */
    @GetMapping("/{memberId}")
    public Response orderList(@PathVariable("memberId") Long memberId) {
        return null;
    }

    /* 주문 내역 상세 조회 */
    @GetMapping("/{memberId}/{orderId}")
    public Response orderDetailList(@PathVariable("memberId")Long memberId, @PathVariable("orderId")Long orderId) {
        return null;
    }

}
