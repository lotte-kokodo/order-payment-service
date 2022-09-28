package shop.kokodo.orderpaymentservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class CartRequest {

    @AllArgsConstructor
    @Getter
    @Setter
    public static class CreateCart {

        private Long productId;
        private Integer qty;
        private Integer unitPrice;
        private Integer memberId;

    }

}
