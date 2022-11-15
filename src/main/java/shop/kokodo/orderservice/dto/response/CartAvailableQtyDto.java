package shop.kokodo.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CartAvailableQtyDto {

    private Long id;
    private Integer qtyAvailable;

}
