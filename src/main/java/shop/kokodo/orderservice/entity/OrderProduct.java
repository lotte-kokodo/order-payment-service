package shop.kokodo.orderservice.entity;

import static javax.persistence.FetchType.LAZY;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.*;
import shop.kokodo.orderservice.dto.request.SingleProductOrderDto;
import shop.kokodo.orderservice.feign.response.OrderProductDto;


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

    public static OrderProduct createOrderProduct(SingleProductOrderDto dto, OrderProductDto orderProductDto) {
        Long productId = dto.getProductId();

        return OrderProduct.builder()
            .memberId(dto.getMemberId())
            .productId(productId)
            .qty(dto.getQty())
            .unitPrice(orderProductDto.getPrice())
            .build();
    }

    public static OrderProduct createOrderProduct(Cart cart, OrderProductDto orderProductDto) {
        return OrderProduct.builder()
            .memberId(cart.getMemberId())
            .productId(cart.getProductId())
            .qty(cart.getQty())
            .unitPrice(orderProductDto.getPrice())
            .build();
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}