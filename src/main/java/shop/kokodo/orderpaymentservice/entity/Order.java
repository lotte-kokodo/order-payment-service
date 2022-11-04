package shop.kokodo.orderpaymentservice.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderpaymentservice.entity.enums.status.OrderStatus;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    private Long memberId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String deliveryMemberName;
    private String deliveryMemberAddress;

    private Integer totalPrice;

    private LocalDateTime orderDate; // 주문 일자

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    public static Order createOrder(Long memberId,
                                String deliveryMemberName,
                                String deliveryMemberAddress,
                                Integer totalPrice,
                                List<OrderProduct> orderProducts) {
        Order order =  Order.builder()
            .deliveryMemberAddress(deliveryMemberAddress)
            .deliveryMemberName(deliveryMemberName)
            .totalPrice(totalPrice)
            .orderDate(LocalDateTime.now())
            .orderStatus(OrderStatus.ORDER_SUCCESS)
            .memberId(memberId)
            .orderProducts(orderProducts)
            .build();
        orderProducts.forEach(orderProduct -> orderProduct.setOrder(order));
        return order;
    }

    @Builder
    private Order(Long memberId,
                            String deliveryMemberName,
                            String deliveryMemberAddress,
                            Integer totalPrice,
                            LocalDateTime orderDate,
                            OrderStatus orderStatus,
                            List<OrderProduct> orderProducts) {

        this.memberId = memberId;
        this.deliveryMemberName = deliveryMemberName;
        this.deliveryMemberAddress = deliveryMemberAddress;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.orderProducts = orderProducts;
    }

    public void changeOrderState(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
