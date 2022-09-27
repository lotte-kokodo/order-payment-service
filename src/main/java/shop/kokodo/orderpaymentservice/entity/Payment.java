package shop.kokodo.orderpaymentservice.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Enumerated
    private PaymentMethod method;

    private Integer installmentMonth;
    private Integer amount;
    private Integer actualAmount;
    private Integer bank;

    @Enumerated
    private PaymentStatus status;

    private String memberName;
    private String memberPhoneNumber;
    private String memberAddress;

    private LocalDateTime paymentDate;

}