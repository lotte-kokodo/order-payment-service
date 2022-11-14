package shop.kokodo.orderservice.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.kokodo.orderservice.dto.response.CartDto;
import shop.kokodo.orderservice.entity.Cart;
import shop.kokodo.orderservice.entity.enums.status.CartStatus;
import shop.kokodo.orderservice.feign.client.ProductServiceClient;
import shop.kokodo.orderservice.feign.response.CartProductDto;
import shop.kokodo.orderservice.repository.interfaces.CartRepository;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("[장바구니] Service")
class CartServiceImplTest {

    @InjectMocks
    CartServiceImpl cartService;

    @Mock
    CartRepository cartRepository;

    @Mock
    ProductServiceClient productServiceClient;

    static Long memberId;
    static MemberIdDto memberIdDto = new MemberIdDto(memberId);
    static List<Long> productIds = List.of(1L, 2L, 3L, 4L, 5L);
    static List<Long> cartIds = List.of(1L, 2L, 3L, 4L, 5L);


    static Long sellerA = 1L;
    static Long sellerB = 2L;

    static CartProductDto productA;
    static CartProductDto productB;
    static CartProductDto productC;
    static CartProductDto productD;
    static CartProductDto productE;

    static Cart cartA;
    static Cart cartB;
    static Cart cartC;
    static Cart cartD;
    static Cart cartE;

    static List<Cart> carts = new ArrayList<>();
    static Map<Long, CartProductDto> productMap = new HashMap<>();

    @BeforeAll
    static void createTestData() {

        productA = CartProductDto.create(1L, "https://file.rankingdak.com/image/RANK/PRODUCT/PRD001/20220928/IMG1664MFn346838529_330_330.jpg", "잇츠나우 실온보관 한입 소스 닭가슴살 갈비 100g", 24900, sellerA);
        productB = CartProductDto.create(2L, "https://file.rankingdak.com/image/RANK/PRODUCT/PRD001/20220826/IMG1661ECG509930627_330_330.jpg", "잇메이트 프로틴 어묵 스테이크 혼합 100g", 21900, sellerA);
        productC = CartProductDto.create(3L, "https://file.rankingdak.com/image/RANK/PRODUCT/PRD001/20220831/IMG1661Kye924587032_330_330.jpg", "네이처엠 현미밥 150g", 18900, sellerB);
        productD = CartProductDto.create(4L, "https://file.rankingdak.com/image/RANK/PRODUCT/PRD001/20220831/IMG1661Nzr924640928_330_330.jpg", "네이처엠 현미밥 200g", 20900, sellerB);
        productE = CartProductDto.create(5L, "https://file.rankingdak.com/image/RANK/PRODUCT/PRD001/20220817/IMG1660Feq719859887_330_330.jpg", "신선애 IQF 생 닭안심살 1kg", 7500, sellerB);

        cartA = Cart.create(1L, memberId, 1L, 1, CartStatus.IN_CART);
        cartB = Cart.create(2L, memberId, 2L, 2, CartStatus.IN_CART);
        cartC = Cart.create(3L, memberId, 3L, 3, CartStatus.IN_CART);
        cartD = Cart.create(4L, memberId, 4L, 4, CartStatus.IN_CART);
        cartE = Cart.create(5L, memberId, 5L, 5, CartStatus.IN_CART);

        carts.add(cartA);
        carts.add(cartB);
        carts.add(cartC);
        carts.add(cartD);
        carts.add(cartE);
        productMap.put(1L, productA);
        productMap.put(2L, productB);
        productMap.put(3L, productC);
        productMap.put(4L, productD);
        productMap.put(5L, productE);
    }



    @Nested
    @DisplayName("장바구니 상품 조회 테스트케이스")
    class GetCart {

        @Test
        @DisplayName("유효한 회원아이디가 입력됐을 때 회원의 장바구니 목록 출력")
        void inputValidMemberIdOutputMemberCarts() {
            // given
            // when
            when(cartRepository.findAllByMemberIdAndCartStatus(memberId, CartStatus.IN_CART)).thenReturn(carts);
            when(productServiceClient.getOrderProducts(productIds)).thenReturn(productMap);

            // [key] 판매자아이디 [value] 판매자상품리스트
            Map<Long, List<CartDto>> sellerCartListMap = cartService.getCarts(memberId);

            // then
            Assertions.assertEquals(sellerCartListMap.get(sellerA).size(), 2);
            Assertions.assertEquals(sellerCartListMap.get(sellerB).size(), 3);

            for (Long sellerId : sellerCartListMap.keySet()) {
                log.info("seller id: {}, carts: {}", sellerId, sellerCartListMap.get(sellerId));
            }
        }

    }

    @Nested
    @DisplayName("실패 로직 테스트 케이스")
    class FailureCase {

    }


}