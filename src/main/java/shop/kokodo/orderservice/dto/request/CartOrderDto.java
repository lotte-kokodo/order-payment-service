package shop.kokodo.orderservice.dto.request;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderservice.message.DtoValidationMessage;

/**
 * 장바구니 상품 주문요청 DTO
 */
@Getter @Setter
@NoArgsConstructor
public class CartOrderDto {

    @NotNull(message = DtoValidationMessage.MEMBER_ID_NULL)
    private Long memberId;

    @NotNull(message = DtoValidationMessage.CART_IDS_NULL)
    private List<Long> cartIds;

    private List<Long> rateCouponIds;
    private List<Long> fixCouponIds;

}
