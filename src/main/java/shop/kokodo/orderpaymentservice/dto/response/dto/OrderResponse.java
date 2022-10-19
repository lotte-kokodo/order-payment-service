package shop.kokodo.orderpaymentservice.dto.response.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;
import shop.kokodo.orderpaymentservice.entity.enums.status.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

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
