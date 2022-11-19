package shop.kokodo.orderservice.feign.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import shop.kokodo.orderservice.entity.OrderProduct;
import shop.kokodo.orderservice.feign.dto.ProductCountDto;
import shop.kokodo.orderservice.feign.response.MonthCountDto;
import shop.kokodo.orderservice.feign.response.MonthlyOrderCountDto;

@Repository
public interface DashboardRepository extends CrudRepository<OrderProduct, Long> {

    @Query("SELECT op.productId AS id, COUNT(*) AS count "
        + "FROM OrderProduct op "
        + "WHERE op.createdDate > CURRENT_DATE "
        + "GROUP BY op.productId")
    <T> List<T> findTodayOrderProductCountMap(Class<T> type);

    @Query("SELECT op.productId AS id, COUNT(*) AS count "
        + "FROM OrderProduct op "
        + "WHERE op.createdDate BETWEEN :start AND :end "
        + "GROUP BY op.productId")
    <T> List<T> findYesterdayOrderProductCountMap(LocalDateTime start, LocalDateTime end, Class<T> type);


    @Query("SELECT function('date_format', op.createdDate, '%Y-%m') AS yearMonth, COUNT(*) AS count FROM OrderProduct op WHERE op.productId IN :productIds GROUP BY yearMonth")
    <T> List<T> findMonthlyOrderCount(List<Long> productIds, Class<T> type);

}
