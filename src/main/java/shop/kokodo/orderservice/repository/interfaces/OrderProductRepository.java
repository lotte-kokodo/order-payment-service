package shop.kokodo.orderservice.repository.interfaces;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import shop.kokodo.orderservice.dto.response.OrderProductThumbnailDto;
import shop.kokodo.orderservice.entity.OrderProduct;

import java.util.List;

@Repository
public interface OrderProductRepository extends CrudRepository<OrderProduct, Long> {

    //추후 querydsl확인
    @Query(value = "SELECT product_id AS ProductId, COUNT(*) AS count, order_id AS OrderId " +
        "FROM order_product as OrderProduct " +
        "WHERE order_id IN :orderId " +
        "GROUP BY order_id", nativeQuery = true)
    List<OrderProductThumbnailDto> findAllByOrderIdIn(List<Long> orderId);

    @Query(value = "SELECT op FROM OrderProduct AS op " +
            " WHERE op.memberId = :memberId" +
            " AND op.order.id = :orderId ")
    List<OrderProduct> findAllByIdAndMemberId(Long memberId, Long orderId);
}
