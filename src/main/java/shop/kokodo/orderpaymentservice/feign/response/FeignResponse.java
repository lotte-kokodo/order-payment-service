package shop.kokodo.orderpaymentservice.feign.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class FeignResponse {

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductPrice {
        Long id;
        Integer price;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberAddress {

        private String address;

    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOfCart {
        private Long id;
        private String thumbnail;
        private String name;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponRateDiscount {
        private Long id;
        private Long rate; // 할인비율
        private Long productId;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponFixDiscount {
        private Long id;
        private Long price; // 할인가격
        private Long productId;
    }
}
