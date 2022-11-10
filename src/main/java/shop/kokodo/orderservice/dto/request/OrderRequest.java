package shop.kokodo.orderservice.dto.request;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class OrderRequest {

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
