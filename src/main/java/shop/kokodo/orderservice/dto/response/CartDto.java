package shop.kokodo.orderservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.kokodo.orderservice.entity.Cart;
import shop.kokodo.orderservice.feign.response.CartProductDto;

@Getter @ToString
@NoArgsConstructor
public class CartDto {
    private Long cartId;
    private Long productId;
    private String productThumbnail;
    private String productName;
    private Integer qty;
    private Integer unitPrice;
    private Long sellerId;


    public static CartDto create (Cart cart, CartProductDto cartProductDto) {
        return new CartDto(cart, cartProductDto);
    }

    @Builder
    public CartDto(Cart cart, CartProductDto cartProductDto) {
        if (cartProductDto == null) {
            this.cartId = cart.getId();
            this.productId = -1L;
        }
        else {
            this.cartId = cart.getId();
            this.productId = cartProductDto.getId();
            this.productThumbnail = cartProductDto.getThumbnail();
            this.productName = cartProductDto.getName();
            this.qty = cart.getQty();
            this.unitPrice = cartProductDto.getPrice();
            this.sellerId = cartProductDto.getSellerId();
        }
    }
}

