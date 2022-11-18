package shop.kokodo.orderservice.kafka.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponNameDto {

    long memberId;
    List<Long> fixCouponIdList;
    List<String> rateCouponNames;

}
