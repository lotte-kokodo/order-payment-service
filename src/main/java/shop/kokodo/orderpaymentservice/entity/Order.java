package shop.kokodo.orderpaymentservice.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
@Builder
public class Order extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    private Long userId;

    @Enumerated
    private OrderStatus orderStatus;

    private String deliveryName;
    private String deliveryAddress;

    private Integer totalPrice;

    private LocalDateTime orderDate; // 주문 일자

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<Cart> carts = new ArrayList<>();

//    @Builder
//    public Order(Long userId, OrderStatus orderStatus, String deliveryName,
//        String deliveryAddress, Integer totalPrice, LocalDateTime orderDate,
//        List<Cart> carts) {
//        this.userId = userId;
//        this.orderStatus = orderStatus;
//        this.deliveryName = deliveryName;
//        this.deliveryAddress = deliveryAddress;
//        this.totalPrice = totalPrice;
//        this.orderDate = orderDate;
//        this.carts = carts;
//    }
}
