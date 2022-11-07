package shop.kokodo.orderpaymentservice.dto.request;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderpaymentservice.message.DtoValidationMessage;

@Getter @Setter
@NoArgsConstructor
public class SingleProductOrderRequest {

    @NotBlank(message = DtoValidationMessage.MEMBER_ID_NULL)
    private Long memberId;

    @NotBlank(message = DtoValidationMessage.PRODUCT_ID_NULL)
    private Long productId;

    @NotBlank(message = DtoValidationMessage.SELLER_ID_BLANK)
    private Long sellerId;

    @NotBlank(message = DtoValidationMessage.QTY_NULL)
    private Integer qty;

    @NotBlank(message = DtoValidationMessage.PRODUCT_SELLER_MAP_NULL)
    private Map<Long, Long> productSellerMap;

    private Long rateCouponId;
    private Long fixCouponId;
}
