package shop.kokodo.orderpaymentservice.dto.response.dto;

import lombok.*;
import shop.kokodo.orderpaymentservice.entity.enums.status.OrderStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailInformationDto {

    private Long id;

    private String name;

    private String thumbnail;

    private int price;

    private int qty;

}
