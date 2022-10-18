package shop.kokodo.orderpaymentservice.dto.response.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.MemberOfOrderSheet;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductOfOrderSheet;

public class OrderResponse {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    @Builder
    public static class OrderSheet {

        List<ProductOfOrderSheet> productInfos;
        MemberOfOrderSheet memberInfo;

    }

}
