package shop.kokodo.orderservice.dto.request;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderservice.message.DtoValidationMessage;

@Getter @Setter
@NoArgsConstructor
public class SingleProductOrderRequest {

    @NotNull(message = DtoValidationMessage.MEMBER_ID_NULL)
    private Long memberId;

    @NotNull(message = DtoValidationMessage.PRODUCT_ID_NULL)
    private Long productId;

    @NotNull(message = DtoValidationMessage.SELLER_ID_BLANK)
    private Long sellerId;

    @NotNull(message = DtoValidationMessage.QTY_NULL)
    private Integer qty;

    @NotNull(message = DtoValidationMessage.PRODUCT_SELLER_MAP_NULL)
    private Map<Long, Long> productSellerMap;

    private Long rateCouponId;
    private Long fixCouponId;

    public SingleProductOrderRequest(Long memberId, Long productId, Long sellerId,
        Integer qty, Map<Long, Long> productSellerMap, Long rateCouponId, Long fixCouponId) {
        this.memberId = memberId;
        this.productId = productId;
        this.sellerId = sellerId;
        this.qty = qty;
        this.productSellerMap = productSellerMap;
        this.rateCouponId = rateCouponId;
        this.fixCouponId = fixCouponId;
    }
}
