package shop.kokodo.orderpaymentservice.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.kokodo.orderpaymentservice.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
