package shop.kokodo.orderpaymentservice.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;

import java.util.List;

@Repository
public interface OrderProductRepository extends CrudRepository<OrderProduct, Long> {
    @Query(value = "SELECT o, op FROM OrderProduct AS op" +
            " LEFT JOIN Order as o" +
            " ON o.id = op.order.id" +
            " WHERE o.memberId = :memberId")
    List<Object[]> findAllByMemberId(Long memberId);

    @Query(value = "SELECT op FROM OrderProduct AS op " +
            " WHERE op.memberId = :memberId" +
            " AND op.order.id = :orderId ")
    List<OrderProduct> findAllByIdAndMemberId(Long memberId, Long orderId);
}
