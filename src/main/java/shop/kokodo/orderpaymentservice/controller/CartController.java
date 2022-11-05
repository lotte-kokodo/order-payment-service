package shop.kokodo.orderpaymentservice.controller;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.kokodo.orderpaymentservice.dto.request.CartRequest;
import shop.kokodo.orderpaymentservice.dto.request.MemberIdDto;
import shop.kokodo.orderpaymentservice.dto.response.dto.CartDto;
import shop.kokodo.orderpaymentservice.dto.response.Response;
import shop.kokodo.orderpaymentservice.dto.response.data.CartResponse;
import shop.kokodo.orderpaymentservice.dto.response.data.ResultMessage;
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
    @PostMapping("/{memberId}")
    public Response createCart(@PathVariable Long memberId,
                                @RequestParam Long productId,
                                @RequestParam Integer qty) {

        Cart cart = cartService.createCart(memberId, productId, qty);

        return Response.success(new ResultMessage(cart.getId(), MessageFormat.CREATE_CART_SUCCESS));
    }

    /* 장바구니 목록 조회 API */
    @GetMapping
    public Response getCarts(@RequestHeader Long memberId) {

        Map<Long, List<CartDto>> carts = cartService.getCarts(memberId);

        return Response.success(carts);
    }

    /* 장바구니 상품 수량 업데이트 API */
    @PostMapping("/{cartId}/qty")
    public Response updateQty(@PathVariable Long cartId, @RequestBody CartRequest.UpdateQty req) {
        CartResponse.UpdateCartQty updateCartQty = cartService.updateQty(cartId, req.getQty());

        return Response.success(updateCartQty);
    }
}
