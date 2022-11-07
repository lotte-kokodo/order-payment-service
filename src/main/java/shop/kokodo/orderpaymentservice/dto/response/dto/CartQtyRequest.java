package shop.kokodo.orderpaymentservice.dto.response.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderpaymentservice.message.DtoValidationMessage;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartQtyRequest {
    @NotNull(message = DtoValidationMessage.MEMBER_ID_NULL)
    private Long memberId;

    @NotNull(message = DtoValidationMessage.CART_ID_NULL)
    private Long cartId;

    @NotNull(message = DtoValidationMessage.QTY_NULL)
    private Integer qty;

}
