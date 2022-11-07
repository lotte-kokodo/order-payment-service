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
import shop.kokodo.orderpaymentservice.dto.request.SingleProductOrderRequest;


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

    public static OrderProduct createOrderProduct(SingleProductOrderRequest dto, Integer unitPrice) {
        return OrderProduct.builder()
            .memberId(dto.getMemberId())
            .productId(dto.getProductId())
            .qty(dto.getQty())
            .unitPrice(unitPrice)
            .build();
    }

    public static OrderProduct createOrderProduct(Cart cart, Integer productPrice) {
        return OrderProduct.builder()
            .memberId(cart.getMemberId())
            .productId(cart.getProductId())
            .qty(cart.getQty())
            .unitPrice(productPrice)
            .build();
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}