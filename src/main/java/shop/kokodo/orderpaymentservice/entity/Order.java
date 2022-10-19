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
@Getter @Setter
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

    @Builder
    public Order(Long memberId,
        OrderStatus orderStatus, String deliveryMemberName, String deliveryMemberAddress,
        Integer totalPrice, LocalDateTime orderDate,
        List<OrderProduct> orderProducts) {
        this.memberId = memberId;
        this.orderStatus = orderStatus;
        this.deliveryMemberName = deliveryMemberName;
        this.deliveryMemberAddress = deliveryMemberAddress;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        for (OrderProduct orderProduct : orderProducts) {
            this.orderProducts.add(orderProduct);
        }
    }
}
