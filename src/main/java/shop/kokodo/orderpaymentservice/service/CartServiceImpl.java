package shop.kokodo.orderpaymentservice.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.kokodo.orderpaymentservice.dto.response.data.CartResponse;
import shop.kokodo.orderpaymentservice.dto.response.data.ResultMessage;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.message.ExceptionMessage;
import shop.kokodo.orderpaymentservice.feign.client.CouponServiceClient;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductOfCart;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductStock;
import shop.kokodo.orderpaymentservice.message.ResponseMessage;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.service.interfaces.CartService;
import shop.kokodo.orderpaymentservice.feign.client.ProductServiceClient;

@Service
@Slf4j
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
//        Map<Long, CouponRateDiscount> rateDiscountMap = couponServiceClient.getCouponRateDiscounts(productIds);
//        Map<Long, CouponFixDiscount> fixDiscountMap = couponServiceClient.getCouponFixDiscounts(productIds);

        return productIds.stream()
                        .map(productId ->
                                CartResponse.GetCart.createGetCartResponse(productCartIdMap.get(productId),
                                                                            cartProductMap.get(productId)))
                        .collect(Collectors.toList());
    }

    @Override
    public ResultMessage updateQty(Long cartId, Integer updatedQty) {
        // 상품 수량을 음수로 요청한 경우
        if (updatedQty < 0) {
            log.error("[CartServiceImpl] 장바구니 상품 업데이트 수량 음수: updated_qty={}", updatedQty);
            throw new IllegalArgumentException(ExceptionMessage.CART_QTY_CANNOT_BE_NEGATIVE);
        }


        Optional<Cart> cart = cartRepository.findById(cartId);

        // 장바구니를 찾을 수 없는 경우
        if (cart.isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.createCartNotFoundMsg(cartId));
        }

        // 장바구니 상품 재고 확인
        Cart foundCart = cart.get();
        ProductStock productStock = productServiceClient.getProductStock(foundCart.getProductId());
        Integer stock = productStock.getStock();

        // 재고가 부족한 경우
        if (stock < updatedQty) {
            log.error("[CartServiceImpl] 장바구니 상품 수량 증가 실패 (상품 재고 부족): product_id={}, stock={}, updatedQty={}",
                productStock.getId(), productStock.getStock(), updatedQty);

            // 주문 가능한 최대 상품 개수로 업데이트
            foundCart.changeQty(stock);
            cartRepository.save(foundCart);

            throw new IllegalArgumentException(ExceptionMessage.createProductOutOfStockMsg(
                productStock.getId()));
        }

        foundCart.changeQty(updatedQty);
        cartRepository.save(foundCart);

        return new ResultMessage(cartId, ResponseMessage.INCREASE_CART_QTY_SUCCESS);
    }

}
