package shop.kokodo.orderservice.controller;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.kokodo.orderservice.aop.MemberInfoCheck;
import shop.kokodo.orderservice.dto.request.CartOrderDto;
import shop.kokodo.orderservice.dto.request.SingleProductOrderDto;
import shop.kokodo.orderservice.controller.response.Response;
import shop.kokodo.orderservice.dto.response.OrderDetailInformationDto;
import shop.kokodo.orderservice.dto.response.OrderInformationDto;
import shop.kokodo.orderservice.dto.response.PagingOrderInformationDto;
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
    @MemberInfoCheck
    @PostMapping("/singleProduct")
    public Response orderSingleProduct(@Valid @RequestBody SingleProductOrderDto req) {
        Order order = orderService.orderSingleProduct(req);
        return Response.success(MessageFormat.CREATE_ORDER_SUCCESS);
    }

    /* 장바구니 주문 API */
    @MemberInfoCheck
    @PostMapping("/cart")
    public Response orderCartProduct(@Valid @RequestBody CartOrderDto req) {
        Order order = orderService.orderCartProducts(req);
        return Response.success(MessageFormat.CREATE_ORDER_SUCCESS);
    }

    /**
     * nativeQuery 사용한 주문 내역 조회
     * @param memberId
     * @return 주문 정보 리스트
     */
    @GetMapping("/")
    public Response orderList(@RequestHeader Long memberId, @RequestParam int page) {
        PagingOrderInformationDto orderInformationDto = orderService.getOrderList(memberId, page - 1);
        return Response.success(orderInformationDto);
    }

    /**
     * queryDsl 사용한 주문 내역 조회
     * @param memberId
     * @return 주문 정보 리스트
     */
    @GetMapping("/querydsl")
    public Response orderListDsl(@RequestHeader Long memberId, @RequestParam int page) {
        PagingOrderInformationDto orderInformationDto = orderService.getOrderListDsl(memberId, page - 1);
        return Response.success(orderInformationDto);
    }

    /**
     * 주문 내역 상세 조회 API
     * @param memberId
     * @param orderId
     * @return 주문 상세 정보 리스트
     */
    @GetMapping("/{orderId}")
    public List<OrderDetailInformationDto> orderDetailList(@RequestHeader Long memberId, @PathVariable("orderId")Long orderId) {
        List<OrderDetailInformationDto> orderDetailInformationDtoList = orderService.getOrderDetailList(memberId, orderId);
        return orderDetailInformationDtoList;
    }

    /**
     * productId로 가격과 갯수를 조회하는 API
     * @param productIdList
     * @return Map<productId, List<qty, unitPrice>>
     */
    @GetMapping("/feign/product")
    public Map<Long, List<Integer>> findByProductId(@RequestParam List<Long> productIdList) {
        return orderService.getProductAllPrice(productIdList);
    }

    /**
     * 사용자가 상품을 주문한 적이 있는지 확인하는 API
     * @param memberId
     * @param productId
     * @return member에 대한 product 주문 여부
     */
    @GetMapping("/check/{memberId}/{productId}")
    public Boolean findByMemberIdAndProductId(@PathVariable Long memberId, @PathVariable Long productId) {
        return orderService.findByMemberIdAndProductId(memberId, productId);
    }
}
