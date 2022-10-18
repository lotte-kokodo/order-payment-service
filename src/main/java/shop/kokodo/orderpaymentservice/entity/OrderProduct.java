package shop.kokodo.orderpaymentservice.entity;

import static javax.persistence.FetchType.LAZY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderProduct extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long memberId;
    private Long productId;

    private Integer qty;
    private Integer unitPrice;

    public static OrderProduct convertCartToOrderProduct(Cart cart) {
        return OrderProduct.builder()
            .memberId(cart.getMemberId())
            .productId(cart.getProductId())
            .qty(cart.getQty())
            .unitPrice(cart.getUnitPrice())
            .build();
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}