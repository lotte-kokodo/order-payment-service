package shop.kokodo.orderpaymentservice.dto.request;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CartRequest {

    /* 장바구니상품 수량 업데이트 요청 DTO */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class UpdateQty {

        @NotBlank
        private Integer qty;

    }

}
