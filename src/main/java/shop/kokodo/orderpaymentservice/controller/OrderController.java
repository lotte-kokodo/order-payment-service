package shop.kokodo.orderpaymentservice.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.kokodo.orderpaymentservice.dto.request.OrderRequest;
import shop.kokodo.orderpaymentservice.dto.response.OrderResponse;
import shop.kokodo.orderpaymentservice.dto.response.Response;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.message.ResponseMessage;
import shop.kokodo.orderpaymentservice.service.interfaces.OrderService;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final ModelMapper mapper;
    private final OrderService orderService;

    @Autowired
    public OrderController(ModelMapper mapper, OrderService orderService) {
        this.mapper = mapper;
        this.orderService = orderService;
    }

    /* 주문 등록 API */
    // TODO 사용자 정보 어디서 넘어오는지 확인
    @PostMapping
    public Response order(@RequestBody OrderRequest.CreateOrder req) {
        Order order = mapper.map(req, Order.class);
        List<Long> cartIds = req.getCartIds();

        Long id = orderService.order(order, cartIds);

        return Response.success(
            new OrderResponse.CreateOrder(id, ResponseMessage.CREATE_ORDER_SUCCESS));
    }

}
