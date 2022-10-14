package shop.kokodo.orderpaymentservice.dto.response.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.FixDiscountPolicy;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.RateDiscountPolicy;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductOfCart;

public class CartResponse {

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GetCart {
        private Long id;
        private Long productId;
        private String productThumbnail;
        private String productName;
        private Integer qty;
        private Integer unitPrice;
        private Integer totalPrice; // 총 가격 (할인 적용 전)
        private Long sellerId;

        public static GetCart createGetCartResponse (Cart cart, ProductOfCart product) {
            return GetCart.builder()
                .id(cart.getId())
                .productId(product.getId())
                .productThumbnail(product.getThumbnail())
                .productName(product.getName())
                .qty(cart.getQty())
                .unitPrice(cart.getUnitPrice())
                .totalPrice(cart.getUnitPrice() * cart.getQty())
                .sellerId(product.getSellerId())
                .build();
        }

    }


    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdateCartQty {
        private Long id;
        private Integer qtyAvailable;
    }

}
