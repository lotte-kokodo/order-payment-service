package shop.kokodo.orderpaymentservice.dto.response.dto;

import lombok.*;
import shop.kokodo.orderpaymentservice.entity.OrderStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderInformationDto {
    //order
    private Long orderId;
    //product
    private String name;
    //order
    private OrderStatus orderStatus;
    //product
    private int price;
    //product
    private String thumbnail;
    //order
    private LocalDateTime orderDate;
}
