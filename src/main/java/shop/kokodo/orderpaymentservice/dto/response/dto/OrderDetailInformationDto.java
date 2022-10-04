package shop.kokodo.orderpaymentservice.dto.response.dto;

import lombok.*;
import shop.kokodo.orderpaymentservice.entity.OrderStatus;

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

    private OrderStatus orderStatus;

}
