package shop.kokodo.orderpaymentservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.kokodo.orderpaymentservice.entity.enums.status.CartStatus;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Cart extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    private Long memberId;
    private Long productId;
    private Integer qty;

    @Enumerated(EnumType.STRING)
    private CartStatus cartStatus;

    public static Cart create(Long id, Long memberId, Long productId, Integer qty,
        CartStatus cartStatus) {
        return new Cart(id, memberId, productId, qty, cartStatus);
    }

    public void changeQty(Integer updatedQty) {
        qty = updatedQty;
    }

    public void changeStatus(CartStatus status) {
        cartStatus = status;
    }
}