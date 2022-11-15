package shop.kokodo.orderservice.feign.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RateCouponDto {
    private Long id;
    private String name;
    private LocalDateTime regdate;
    private Integer rate;
    private Integer minPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long productId;
    private Long sellerId;

    public RateCouponDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
