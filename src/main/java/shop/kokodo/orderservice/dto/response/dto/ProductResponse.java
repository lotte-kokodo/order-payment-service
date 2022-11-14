package shop.kokodo.orderservice.dto.response.dto;

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

    private String name;
    private String displayName;
    private String thumbnail;

}
