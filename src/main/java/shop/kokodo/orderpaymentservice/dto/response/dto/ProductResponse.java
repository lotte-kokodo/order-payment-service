package shop.kokodo.orderpaymentservice.dto.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Long productId;
    private Integer unitPrice;

}
