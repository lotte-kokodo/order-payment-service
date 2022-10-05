package shop.kokodo.orderpaymentservice.dto.request;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class OrderRequest {

    /* 장바구니상품 주문 요청 DTO */
    @AllArgsConstructor
    @Getter
    @Setter
    public static class CreateCartOrder {

        @NotBlank
        private List<Long> cartIds;

        @NotBlank
        private List<Long> couponIds;

    }

}
