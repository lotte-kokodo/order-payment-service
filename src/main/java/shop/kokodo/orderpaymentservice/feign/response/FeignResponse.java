package shop.kokodo.orderpaymentservice.feign.response;

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
