package shop.kokodo.orderservice.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.kokodo.orderservice.message.DtoValidationMessage;

/**
 * 장바구니 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    @NotNull(message = DtoValidationMessage.MEMBER_ID_NULL)
    private Long memberId;

    @NotNull(message = DtoValidationMessage.PRODUCT_ID_NULL)
    private Long productId;

    @NotNull(message = DtoValidationMessage.QTY_NULL)
    private Integer qty;

}
