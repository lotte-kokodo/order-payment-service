package shop.kokodo.orderpaymentservice.repository.interfaces;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import shop.kokodo.orderpaymentservice.entity.Cart;

@Repository
public interface CartRepository extends CrudRepository<Cart, Long> {

    /* 장바구니 상품 아이디 리스트 기반으로 장바구니 상품 가져오기 */
    List<Cart> findByIdIn(List<Long> ids);
}
