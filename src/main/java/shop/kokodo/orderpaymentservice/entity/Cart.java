package shop.kokodo.orderpaymentservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    private Long productId;

    private Integer qty;

    private Integer unitPrice;

    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;

    public void setOrder(Order order) {
        this.order = order;
    }
//
//    @Builder
//    public Cart(Long productId, Integer qty, Integer unitPrice, Long memberId,
//        Order order) {
//        this.productId = productId;
//        this.qty = qty;
//        this.unitPrice = unitPrice;
//        this.memberId = memberId;
//        this.order = order;
//    }
}