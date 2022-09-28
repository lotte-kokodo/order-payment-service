package shop.kokodo.orderpaymentservice.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.kokodo.orderpaymentservice.dto.request.CartRequest;
import shop.kokodo.orderpaymentservice.dto.response.CartResponse;
import shop.kokodo.orderpaymentservice.dto.response.Response;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.message.ResponseMessage;
import shop.kokodo.orderpaymentservice.service.interfaces.CartService;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final ModelMapper mapper;
    private final CartService cartService;

    @Autowired
    public CartController(ModelMapper mapper, CartService cartService) {
        this.mapper = mapper;
        this.cartService = cartService;
    }

    @PostMapping
    public Response createCart(@RequestBody CartRequest.CreateCart req) {
        Cart cart = mapper.map(req, Cart.class);
        Long id = cartService.createCart(cart);

        return Response.success(new CartResponse.CreateCart(id, ResponseMessage.CREATE_CART_SUCCESS));
    }


}
