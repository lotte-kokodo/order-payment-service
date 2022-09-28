package shop.kokodo.orderpaymentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class CartResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CreateCart {

        private Long id;
        private String msg;

    }

}
