package shop.kokodo.orderservice.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.kokodo.orderservice.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
