package shop.kokodo.orderpaymentservice.dto.response.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderpaymentservice.message.DtoValidationMessage;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartQtyDto {

    @NotBlank(message = DtoValidationMessage.CART_ID_BLANK)
    private Long cartId;

    @NotBlank(message = DtoValidationMessage.QTY_BLANK)
    private Integer qty;

}
