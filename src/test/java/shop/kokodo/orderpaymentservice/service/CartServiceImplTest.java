package shop.kokodo.orderpaymentservice.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("[장바구니] Service")
class CartServiceImplTest {

    @InjectMocks
    CartServiceImpl cartService;

    @Mock
    CartRepository cartRepository;

    static Long memberId = 1L;
    static Long productId = 1L;
    static Integer qty = 350;

    @Nested
    @DisplayName("성공 로직 테스트 케이스")
    class SuccessCase {

        @Test
        @DisplayName("장바구니 저장")
        void createCart() {
            // given
            Cart cart = Cart.builder()
                .memberId(memberId)
                .productId(productId)
                .qty(qty)
                .build();

            // when
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);
            Long cartId = cartService.createCart(memberId, productId, qty);

            // then
            assertNull(cartId);
        }

    }

    @Nested
    @DisplayName("실패 로직 테스트 케이스")
    class FailureCase {

    }


}