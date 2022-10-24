package shop.kokodo.orderpaymentservice.dto.request;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class OrderRequest {

    /* 장바구니상품 주문 요청 DTO */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class CreateCartOrder {

        @NotBlank
        private List<Long> cartIds;

        @NotBlank
        private Map<Long, Long> productSellerMap;

        @NotBlank
        private List<Long> rateCouponIds;

        @NotBlank
        private List<Long> fixCouponIds;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class GetOrderSheet {
        private Long productId;
        private Integer qty;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class CouponProductPrice {
        private Long couponId;
        private Integer productPrice;
    }
}
