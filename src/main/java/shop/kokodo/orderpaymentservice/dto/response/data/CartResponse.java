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
    public static class UpdateCartQty {
        private Long id;
        private Integer qtyAvailable;
    }

}
