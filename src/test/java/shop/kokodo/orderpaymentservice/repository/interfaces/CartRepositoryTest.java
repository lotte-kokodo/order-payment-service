package shop.kokodo.orderpaymentservice.repository.interfaces;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import shop.kokodo.orderpaymentservice.entity.Cart;

@DataJpaTest
@DisplayName("[장바구니] Repository")
class CartRepositoryTest {

    @Autowired
    CartRepository cartRepository;

    static Long memberId = 1L;
    static Long productId = 1L;
    static Integer qty = 350;

    @Nested
    @DisplayName("성공 로직 테스트 케이스")
    class SuccessCase {

        @Test
        @DisplayName("장바구니 저장")
        void save() {
            // given
            Cart cart = Cart.builder()
                .memberId(memberId)
                .productId(productId)
                .qty(qty)
                .build();

            // when
            Cart createdCart = cartRepository.save(cart);

            // then
            assertNotNull(createdCart.getId()); // 주문 아이디가 부여됐는지 확인
        }

    }

    @Nested
    @DisplayName("실패 로직 테스트 케이스")
    class FailureCase {

    }


}