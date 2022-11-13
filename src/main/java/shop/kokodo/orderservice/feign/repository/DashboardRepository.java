package shop.kokodo.orderservice.feign.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import shop.kokodo.orderservice.entity.Order;

@Repository
public interface DashboardRepository extends CrudRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.orderDate > CURRENT_DATE")
    List<Order> findTodayOrder();

}
