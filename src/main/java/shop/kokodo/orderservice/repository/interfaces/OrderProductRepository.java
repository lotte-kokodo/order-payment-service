package shop.kokodo.orderservice.repository.interfaces;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import shop.kokodo.orderservice.dto.response.OrderProductThumbnailDto;
import shop.kokodo.orderservice.entity.Order;
import shop.kokodo.orderservice.entity.OrderProduct;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderProductRepository extends CrudRepository<OrderProduct, Long> {

    @Query(value = "SELECT product_id AS ProductId, COUNT(*) AS count, order_id AS OrderId " +
        "FROM order_product as OrderProduct " +
        "WHERE order_id IN :orderId " +
        "GROUP BY order_id", nativeQuery = true)
    List<OrderProductThumbnailDto> findAllByOrderIdIn(List<Long> orderId);

    @Query(value = "SELECT op FROM OrderProduct AS op " +
            " WHERE op.memberId = :memberId" +
            " AND op.order.id = :orderId " +
            " ORDER BY op.order.orderDate DESC")
    List<OrderProduct> findAllByIdAndMemberId(Long memberId, Long orderId);

    @Query(value = "SELECT op FROM OrderProduct op WHERE " +
            "op.order.orderDate BETWEEN :startDate AND :endDate AND " +
            "op.productId IN (:productIdList)")
    List<OrderProduct> findByProductIdListAndSellerId(List<Long> productIdList, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT op FROM OrderProduct op WHERE " +
            "op.order.memberId = :memberId AND " +
            "op.productId = :productId")
    List<OrderProduct> findByMemberIdAndProductId(Long memberId, Long productId);
}
