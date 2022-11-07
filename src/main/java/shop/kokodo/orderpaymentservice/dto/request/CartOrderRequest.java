package shop.kokodo.orderpaymentservice.dto.request;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderpaymentservice.message.DtoValidationMessage;

/**
 * 장바구니 상품 주문요청 DTO
 */
@Getter @Setter
@NoArgsConstructor
public class CartOrderRequest {

    @NotBlank(message = DtoValidationMessage.MEMBER_ID_NULL)
    private Long memberId;

    @NotEmpty(message = DtoValidationMessage.CART_IDS_NULL)
    private List<Long> cartIds;

    @NotEmpty(message = DtoValidationMessage.PRODUCT_SELLER_MAP_NULL)
    private Map<Long, Long> productSellerMap;

    private List<Long> rateCouponIds;
    private List<Long> fixCouponIds;

}
