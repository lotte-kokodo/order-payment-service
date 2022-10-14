package shop.kokodo.orderpaymentservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private Long memberId;
    private Long productId;
    private Integer qty;
    private Integer unitPrice;

    public void changeQty(Integer updatedQty) {
        qty = updatedQty;
    }

    public void increaseQty() {
        qty++;
    }

    public void decreaseQty() {
        qty--;
    }
}