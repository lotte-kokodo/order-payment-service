package shop.kokodo.orderpaymentservice.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.kokodo.orderpaymentservice.dto.response.data.CartResponse;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.feign.client.CouponServiceClient;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.CouponFixDiscount;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.CouponRateDiscount;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductOfCart;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.service.interfaces.CartService;
import shop.kokodo.orderpaymentservice.feign.client.ProductServiceClient;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final ProductServiceClient productServiceClient;

    private final CouponServiceClient couponServiceClient;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
        ProductServiceClient productServiceClient,
        CouponServiceClient couponServiceClient) {
        this.cartRepository = cartRepository;
        this.productServiceClient = productServiceClient;
        this.couponServiceClient = couponServiceClient;
    }

    @Transactional
    public Cart createCart(Long memberId, Long productId, Integer qty) {

        // 상품 가격
        FeignResponse.ProductPrice productPrice = productServiceClient.getProduct(productId);
        Integer unitPrice = productPrice.getPrice();

        Cart cart = Cart.builder()
            .memberId(memberId)
            .productId(productId)
            .qty(qty)
            .unitPrice(unitPrice)
            .build();

        cartRepository.save(cart);

        return cart;
    }

    @Override
    public List<CartResponse.GetCart> getCarts(Long memberId) {
        /* 장바구니 조회 */
        List<Cart> carts = cartRepository.findAllByMemberId(memberId);

        // productId List 생성
        List<Long> productIds = carts.stream().map(Cart::getProductId).collect(Collectors.toList());

        // productId(key)-cartId(value) Map 생성
        Map<Long, Cart> productCartIdMap = carts.stream()
            .collect(Collectors.toMap(Cart::getProductId, Function.identity()));

        // 상품 조회
        Map<Long, ProductOfCart> cartProductMap = productServiceClient.getCartProducts(productIds);

        /* 쿠폰 조회 (productId 에 해당되는 상품 적용) */
        // 쿠폰 정책 (비율, 고정금액) 2가지 받아야 한다.
        // 할인비율(비율할인정책), 할인가격(고정할인정책) 모두 보내기
        Map<Long, CouponRateDiscount> rateDiscountMap = couponServiceClient.getCouponRateDiscounts(productIds);
        Map<Long, CouponFixDiscount> fixDiscountMap = couponServiceClient.getCouponFixDiscounts(productIds);

        return productIds.stream()
                        .map(productId ->
                                CartResponse.GetCart.createGetCartResponse(productCartIdMap.get(productId),
                                                                            cartProductMap.get(productId),
                                                                            rateDiscountMap.get(productId),
                                                                            fixDiscountMap.get(productId)))
                        .collect(Collectors.toList());
    }
}
