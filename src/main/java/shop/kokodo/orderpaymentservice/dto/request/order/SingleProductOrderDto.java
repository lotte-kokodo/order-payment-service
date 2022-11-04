package shop.kokodo.orderpaymentservice.dto.request.order;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderpaymentservice.message.DtoValidationMessage;

@Getter @Setter
@NoArgsConstructor
public class SingleProductOrderDto {

    @NotBlank(message = DtoValidationMessage.MEMBER_ID_NOT_BLANK)
    private Long memberId;

    @NotBlank(message = DtoValidationMessage.PRODUCT_ID_BLANK)
    private Long productId;

    @NotBlank(message = DtoValidationMessage.SELLER_ID_BLANK)
    private Long sellerId;

    @NotBlank(message = DtoValidationMessage.QTY_BLANK)
    private Integer qty;

    @NotBlank(message = DtoValidationMessage.PRODUCT_SELLER_MAP_EMPTY)
    private Map<Long, Long> productSellerMap;

    private Long rateCouponId;
    private Long fixCouponId;
}
