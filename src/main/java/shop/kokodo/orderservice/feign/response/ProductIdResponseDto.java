package shop.kokodo.orderservice.feign.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductIdResponseDto {

    private List<Long> todayOrderProductIds;
    private List<Long> yesterdayOrderProductIds;

}
