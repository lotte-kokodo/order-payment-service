package shop.kokodo.orderservice.feign.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDto {

    private Long id;
    private Integer price;
    private Long sellerId;

}
