package shop.kokodo.orderpaymentservice.dto.request.order;

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
public class CartOrderDto {

    @NotBlank(message = DtoValidationMessage.MEMBER_ID_NOT_BLANK)
    private Long memberId;

    @NotEmpty(message = DtoValidationMessage.CART_IDS_EMPTY)
    private List<Long> cartIds;

    @NotEmpty(message = DtoValidationMessage.PRODUCT_SELLER_MAP_EMPTY)
    private Map<Long, Long> productSellerMap;

    private List<Long> rateCouponIds;
    private List<Long> fixCouponIds;

}
