package shop.kokodo.orderpaymentservice.dto.response.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.feign.response.ProductDto;

@Getter @ToString
@NoArgsConstructor
public class CartResponse {
    private Long cartId;
    private Long productId;
    private String productThumbnail;
    private String productName;
    private Integer qty;
    private Integer unitPrice;
    private Long sellerId;


    public static CartResponse create (Cart cart, ProductDto productDto) {
        return new CartResponse(cart, productDto);
    }

    @Builder
    public CartResponse(Cart cart, ProductDto productDto) {
        this.cartId = cart.getId();
        this.productId = productDto.getId();
        this.productThumbnail = productDto.getThumbnail();
        this.productName = productDto.getName();
        this.qty = cart.getQty();
        this.unitPrice = productDto.getPrice();
        this.sellerId = productDto.getSellerId();
    }
}

