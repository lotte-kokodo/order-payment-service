package shop.kokodo.orderpaymentservice.service.interfaces;


import java.util.List;
import shop.kokodo.orderpaymentservice.dto.response.OrderResponse;
import shop.kokodo.orderpaymentservice.entity.Order;

public interface OrderService {

    /* 주문 등록 비즈니스 로직 */
    Long order(Order order, List<Long> cartIds);

}
