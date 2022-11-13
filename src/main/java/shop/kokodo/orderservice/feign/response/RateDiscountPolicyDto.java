package shop.kokodo.orderservice.feign.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RateDiscountPolicyDto {
    private Long rateDiscountPolicyId;
    private Integer rate; // 할인비율
    private Long productId;
}
