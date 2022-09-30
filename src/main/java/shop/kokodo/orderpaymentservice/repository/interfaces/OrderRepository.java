package shop.kokodo.orderpaymentservice.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value="SELECT o, op " +
            "FROM OrderProduct op LEFT JOIN op.order o " +
            "WHERE op.id = o.id " +
            "and o.id = :memberId")
    List<Order> findByMemberId(Long memberId);
}
