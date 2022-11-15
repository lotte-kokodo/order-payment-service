package shop.kokodo.orderservice.dto.response;

import lombok.*;

/**
 * packageName : shop.kokodo.orderservice.dto.response.dto
 * fileName : shop.kokodo.orderservice.dto.response.OrderProductDslDto
 * author : BTC-N24
 * date : 2022-11-15
 * description :
 * ======================================================
 * DATE                AUTHOR                NOTE
 * ======================================================
 * 2022-11-15           BTC-N24              최초 생성
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProductDslDto {
    Long productId;

    Long orderId;

    Long count;
}
