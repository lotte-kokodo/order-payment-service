package shop.kokodo.orderservice.feign.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCountRequestDto {

    private Long sellerId;
    private List<Long> todayProductIds;
    private List<Long> yesterdayProductIds;

}
