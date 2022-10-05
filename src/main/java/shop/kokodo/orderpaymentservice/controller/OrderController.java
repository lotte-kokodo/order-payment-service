package shop.kokodo.orderpaymentservice.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.kokodo.orderpaymentservice.dto.request.OrderRequest;
import shop.kokodo.orderpaymentservice.dto.response.Response;
import shop.kokodo.orderpaymentservice.dto.response.dto.IdAndMessageDto;
import shop.kokodo.orderpaymentservice.message.ResponseMessage;
import shop.kokodo.orderpaymentservice.service.interfaces.OrderService;

@Slf4j
@RestController
@RequestMapping("/order-service")
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
                                        @RequestParam("qty") Integer qty,
                                        @RequestParam("couponId") Long couponId) {

        Long orderId = orderService.orderSingleProduct(memberId, productId, qty, couponId);

        return Response.success(new IdAndMessageDto.CreateSuccess(orderId,
            ResponseMessage.CREATE_ORDER_SUCCESS));

    }

    /* 장바구니 주문 API */
    // TODO 사용자 정보 어디서 넘어오는지 확인
    @PostMapping("/{memberId}/cart")
    public Response orderCartProduct(@PathVariable("memberId") Long memberId,
                                    @RequestBody OrderRequest.CreateCartOrder req) {

        List<Long> cartIds = req.getCartIds();
        List<Long> couponIds = req.getCouponIds();

        Long orderId = orderService.orderCartProducts(memberId, cartIds, couponIds);

        return Response.success(
            new IdAndMessageDto.CreateSuccess(orderId, ResponseMessage.CREATE_ORDER_SUCCESS));
    }

}
