package shop.kokodo.orderpaymentservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.kokodo.orderpaymentservice.dto.response.Response;
import shop.kokodo.orderpaymentservice.dto.response.dto.IdAndMessageDto;
import shop.kokodo.orderpaymentservice.message.ResponseMessage;
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

    @PostMapping("/{memberId}")
    public Response createCart(@PathVariable Long memberId,
                                @RequestParam Long productId,
                                @RequestParam Integer qty) {

        Long cartId = cartService.createCart(memberId, productId, qty);

        return Response.success(new IdAndMessageDto.CreateSuccess(cartId,
            ResponseMessage.CREATE_CART_SUCCESS));
    }


}
