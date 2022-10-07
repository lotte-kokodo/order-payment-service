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
    public static class MemberDeliveryInfo {

        private String memberName;
        private String memberAddress;

    }

}
