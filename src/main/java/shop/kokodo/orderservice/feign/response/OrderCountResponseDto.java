package shop.kokodo.orderservice.feign.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCountResponseDto {

    private Integer todayOrderCount;
    private Integer yesterdayOrderCount;

}
