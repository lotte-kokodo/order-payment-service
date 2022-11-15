package shop.kokodo.orderservice.dto.response;

import lombok.*;
import shop.kokodo.orderservice.entity.enums.status.OrderStatus;

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
