package shop.kokodo.orderservice.dto.response.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderservice.feign.response.FeignResponse.RateDiscountPolicy;
import shop.kokodo.orderservice.feign.response.ProductDto;

public class OrderResponse {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    @Builder
    public static class GetOrderProduct {

        private Long productId;
        private String productThumbnail;
        private String productName;
        private Integer unitPrice;
        private Long sellerId;

        private Integer discRate;

        public static GetOrderProduct createGetOrderProduct (ProductDto product,
                                                    RateDiscountPolicy rateDiscountPolicy) {

            Integer dRate = (rateDiscountPolicy != null) ? rateDiscountPolicy.getRate() : 0;
            boolean isDisc = (dRate != 0);

            return GetOrderProduct.builder()
                .productId(product.getId())
                .productThumbnail(product.getThumbnail())
                .productName(product.getName())
                .unitPrice(product.getPrice())
                .sellerId(product.getSellerId())
                .discRate(isDisc ? dRate : 0)
                .build();
        }

    }

}
