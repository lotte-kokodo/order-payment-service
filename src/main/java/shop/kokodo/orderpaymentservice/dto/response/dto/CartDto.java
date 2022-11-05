package shop.kokodo.orderpaymentservice.dto.response.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.feign.response.ProductDto;

@Getter @ToString
@NoArgsConstructor
public class CartDto {
    private Long cartId;
    private Long productId;
    private String productThumbnail;
    private String productName;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice; // 총 가격 (할인 적용 전)
    private Long sellerId;


    public static CartDto create (Cart cart, ProductDto productDto) {
        return new CartDto(cart, productDto);
    }

    @Builder
    public CartDto(Cart cart, ProductDto productDto) {
        this.cartId = cart.getId();
        this.productId = productDto.getId();
        this.productThumbnail = productDto.getThumbnail();
        this.productName = productDto.getName();
        this.qty = cart.getQty();
        this.unitPrice = cart.getUnitPrice();
        this.totalPrice = cart.getUnitPrice()*cart.getQty();
        this.sellerId = productDto.getSellerId();
    }
}

