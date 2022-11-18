package shop.kokodo.orderservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.kokodo.orderservice.entity.Cart;
import shop.kokodo.orderservice.feign.response.CartProductDto;

@Getter @ToString
@NoArgsConstructor
public class CartResponseDto {
    private Long cartId;
    private Long productId;
    private String productThumbnail;
    private String productName;
    private Integer qty;
    private Integer unitPrice;
    private Long sellerId;


    public static CartResponseDto create (Cart cart, CartProductDto cartProductDto) {
        return new CartResponseDto(cart, cartProductDto);
    }

    @Builder
    public CartResponseDto(Cart cart, CartProductDto cartProductDto) {
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

