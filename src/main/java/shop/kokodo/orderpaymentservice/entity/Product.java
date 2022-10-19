package shop.kokodo.orderpaymentservice.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Product extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    Long id;

    String name;

    Integer price;

    String displayName;

    int stock;

    LocalDateTime deadline;

    String thumbnail;

    Long sellerId;

    Integer deliveryFee;
}
