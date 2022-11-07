package shop.kokodo.orderpaymentservice.controller;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import shop.kokodo.orderpaymentservice.dto.request.CartRequest;
import shop.kokodo.orderpaymentservice.dto.response.Response;
import shop.kokodo.orderpaymentservice.dto.response.data.ResultMessage;
import shop.kokodo.orderpaymentservice.dto.response.dto.CartAvailableQtyResponse;
import shop.kokodo.orderpaymentservice.dto.response.dto.CartResponse;
import shop.kokodo.orderpaymentservice.dto.response.dto.CartQtyRequest;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.message.MessageFormat;
import shop.kokodo.orderpaymentservice.service.interfaces.CartService;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(
        CartService cartService) {
        this.cartService = cartService;
    }

    /* 장바구니 상품 생성 API */
    @PostMapping
    public Response createCart(@Valid @RequestBody CartRequest req) {

        Cart cart = cartService.createCart(req);

        return Response.success(new ResultMessage(cart.getId(), MessageFormat.CREATE_CART_SUCCESS));
    }

    /* 장바구니 목록 조회 API */
    @GetMapping
    public Response getCarts(@RequestHeader Long memberId) {

        Map<Long, List<CartResponse>> carts = cartService.getCarts(memberId);

        return Response.success(carts);
    }

    /* 장바구니 상품 수량 업데이트 API */
    @PostMapping("/qty")
    public Response updateQty(@Valid @RequestBody CartQtyRequest req) {
        CartAvailableQtyResponse updateCartQty = cartService.updateQty(req);

        return Response.success(updateCartQty);
    }
}
