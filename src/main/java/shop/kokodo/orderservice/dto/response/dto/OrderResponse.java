package shop.kokodo.orderservice.dto.response.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import shop.kokodo.orderservice.entity.OrderProduct;
import shop.kokodo.orderservice.entity.enums.status.OrderStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private OrderStatus orderStatus;
    private Integer totalPrice;
    private LocalDateTime orderDate;
    private OrderProduct representOrderProduct;
    private Integer orderProductCount;
}
