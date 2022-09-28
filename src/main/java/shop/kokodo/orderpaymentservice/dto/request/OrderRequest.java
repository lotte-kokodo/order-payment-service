package shop.kokodo.orderpaymentservice.dto.request;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class OrderRequest {


    @Getter
    public static class CreateOrder {

        @NotBlank
        private List<Long> cartIds;

        @NotBlank
        private Long userId;

        @NotBlank
        private String deliveryName;

        @NotBlank
        private String deliveryAddress;

        @NotBlank
        private Integer totalPrice;

    }






}
