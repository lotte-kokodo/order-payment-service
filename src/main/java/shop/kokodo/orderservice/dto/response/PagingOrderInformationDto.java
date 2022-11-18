package shop.kokodo.orderservice.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * packageName : shop.kokodo.orderservice.dto.response
 * fileName : PagingOrderInformationDto
 * author : BTC-N24
 * date : 2022-11-17
 * description :
 * ======================================================
 * DATE                AUTHOR                NOTE
 * ======================================================
 * 2022-11-17           BTC-N24              최초 생성
 */
@Data
@NoArgsConstructor
public class PagingOrderInformationDto {
    long totalCount;

    List<OrderInformationDto> orderInformationDtoList;

    @Builder
    public PagingOrderInformationDto(long totalCount, List<OrderInformationDto> orderInformationDtoList) {
        this.totalCount = totalCount;
        this.orderInformationDtoList = orderInformationDtoList;
    }
}
