package shop.kokodo.orderpaymentservice.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.kokodo.orderpaymentservice.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
