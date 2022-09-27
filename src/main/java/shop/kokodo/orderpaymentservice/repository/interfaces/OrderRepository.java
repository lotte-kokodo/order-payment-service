package shop.kokodo.orderpaymentservice.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.kokodo.orderpaymentservice.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
