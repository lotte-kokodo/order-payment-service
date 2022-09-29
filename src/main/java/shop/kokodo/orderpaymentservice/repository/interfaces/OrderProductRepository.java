package shop.kokodo.orderpaymentservice.repository.interfaces;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;

@Repository
public interface OrderProductRepository extends CrudRepository<OrderProduct, Long> {
}
