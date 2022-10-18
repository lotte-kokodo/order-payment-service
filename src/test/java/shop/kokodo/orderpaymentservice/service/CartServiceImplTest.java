package shop.kokodo.orderpaymentservice.service;

import static org.junit.jupiter.api.Assertions.*;
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
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.feign.client.ProductServiceClient;

@ExtendWith(MockitoExtension.class)
@DisplayName("[장바구니] Service")
class CartServiceImplTest {

    @InjectMocks
    CartServiceImpl cartService;

    @Mock
    CartRepository cartRepository;

    @Mock
    ProductServiceClient productServiceClient;


    @Nested
    @DisplayName("성공 로직 테스트 케이스")
    class SuccessCase {

        @Test
        @DisplayName("장바구니 저장")
        void createCart() {
            // given
            Long memberId = 1L;
            Long productId = 1L;
            Integer qty = 350;

            Integer price = 2000;

            FeignResponse.ProductPrice productPrice = new FeignResponse.ProductPrice(productId, price);
            when(productServiceClient.getProduct(productId))
                .thenReturn(productPrice);

            Cart cart = Cart.builder()
                .memberId(memberId)
                .productId(productId)
                .qty(qty)
                .unitPrice(productPrice.getPrice())
                .build();

            // when
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);
            Cart result = cartService.createCart(memberId, productId, qty);

            // then
            assertNotNull(result);
        }

    }

    @Nested
    @DisplayName("실패 로직 테스트 케이스")
    class FailureCase {

    }


}