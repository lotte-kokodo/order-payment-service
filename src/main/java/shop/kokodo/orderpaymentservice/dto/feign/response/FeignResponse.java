package shop.kokodo.orderpaymentservice.dto.feign.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class FeignResponse {

    @Getter @Setter
    @NoArgsConstructor
    public static class ProductPrice {
        Long id;
        Integer price;
    }

}
