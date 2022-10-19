package shop.kokodo.orderpaymentservice.dto.response.dto;

import lombok.*;
import shop.kokodo.orderpaymentservice.entity.enums.status.OrderStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderInformationDto {

    private Long orderId;

    private String name;

    private OrderStatus orderStatus;

    private int price;

    private String thumbnail;

    private LocalDateTime orderDate;

}
