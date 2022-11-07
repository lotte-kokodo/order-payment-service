package shop.kokodo.orderpaymentservice.dto.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CartAvailableQtyResponse {

    private Long id;
    private Integer qtyAvailable;

}
