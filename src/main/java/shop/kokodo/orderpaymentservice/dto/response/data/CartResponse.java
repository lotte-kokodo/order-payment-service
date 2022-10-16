package shop.kokodo.orderpaymentservice.dto.response.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.RateDiscountPolicy;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductOfOrder;

public class CartResponse {

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GetCart {
        private Long cartId;
        private Long productId;
        private String productThumbnail;
        private String productName;
        private Integer qty;
        private Integer unitPrice;
        private Integer totalPrice; // 총 가격 (할인 적용 전)
        private Long sellerId;

        private Integer discRate;
        private Integer discPrice;

        public static GetCart createGetCartResponse (Cart cart, ProductOfOrder product,
            RateDiscountPolicy rateDiscountPolicy) {

            Integer tPrice = cart.getUnitPrice()*cart.getQty();
            Integer dRate = (rateDiscountPolicy != null) ? rateDiscountPolicy.getRate() : 0;
            boolean isDisc = (dRate != 0);

            return GetCart.builder()
                .cartId(cart.getId())
                .productId(product.getId())
                .productThumbnail(product.getThumbnail())
                .productName(product.getName())
                .qty(cart.getQty())
                .unitPrice(cart.getUnitPrice())
                .totalPrice(tPrice)
                .sellerId(product.getSellerId())
                .discRate(isDisc ? dRate : 0)
                .discPrice(isDisc ? (int) (tPrice*dRate*0.01) : 0)
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
