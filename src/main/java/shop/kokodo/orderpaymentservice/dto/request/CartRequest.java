package shop.kokodo.orderpaymentservice.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.kokodo.orderpaymentservice.message.DtoValidationMessage;

/**
 * 장바구니 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {

    @NotNull(message = DtoValidationMessage.MEMBER_ID_NULL)
    private Long memberId;

    @NotNull(message = DtoValidationMessage.PRODUCT_ID_NULL)
    private Long productId;

    @NotNull(message = DtoValidationMessage.QTY_NULL)
    private Integer qty;

}
