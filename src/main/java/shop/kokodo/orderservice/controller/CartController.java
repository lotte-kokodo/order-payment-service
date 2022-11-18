package shop.kokodo.orderservice.controller;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.kokodo.orderservice.dto.request.CartRequestDto;
import shop.kokodo.orderservice.controller.response.Response;
import shop.kokodo.orderservice.dto.response.CartAvailableQtyDto;
import shop.kokodo.orderservice.dto.request.CartQtyDto;
import shop.kokodo.orderservice.dto.response.CartResponseDto;
import shop.kokodo.orderservice.entity.Cart;
import shop.kokodo.orderservice.message.MessageFormat;
import shop.kokodo.orderservice.service.interfaces.CartService;

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
    public Response createCart(@Valid @RequestBody CartRequestDto req) {

        Cart cart = cartService.createCart(req);

        return Response.success(MessageFormat.CREATE_CART_SUCCESS);
    }

    /* 장바구니 목록 조회 API */
    @GetMapping
    public Response getCarts(@RequestHeader Long memberId) {

        Map<Long, List<CartResponseDto>> carts = cartService.getCarts(memberId);

        return Response.success(carts);
    }

    /* 장바구니 상품 수량 업데이트 API */
    @PostMapping("/qty")
    public Response updateQty(@Valid @RequestBody CartQtyDto req) {
        CartAvailableQtyDto updateCartQty = cartService.updateQty(req);

        return Response.success(updateCartQty);
    }
}
