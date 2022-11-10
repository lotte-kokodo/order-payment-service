package shop.kokodo.orderservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.kokodo.orderservice.dto.request.CartRequest;
import shop.kokodo.orderservice.dto.response.dto.CartAvailableQtyResponse;
import shop.kokodo.orderservice.dto.response.dto.CartResponse;
import shop.kokodo.orderservice.dto.response.dto.CartQtyRequest;
import shop.kokodo.orderservice.entity.Cart;
import shop.kokodo.orderservice.entity.enums.status.CartStatus;
import shop.kokodo.orderservice.exception.api.ApiRequestException;
import shop.kokodo.orderservice.feign.client.ProductServiceClient;
import shop.kokodo.orderservice.feign.client.PromotionServiceClient;
import shop.kokodo.orderservice.feign.response.FeignResponse.ProductStock;
import shop.kokodo.orderservice.feign.response.ProductDto;
import shop.kokodo.orderservice.message.ExceptionMessage;
import shop.kokodo.orderservice.repository.interfaces.CartRepository;
import shop.kokodo.orderservice.service.interfaces.CartService;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final ProductServiceClient productServiceClient;

    private final PromotionServiceClient promotionServiceClient;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
        ProductServiceClient productServiceClient,
        PromotionServiceClient promotionServiceClient) {
        this.cartRepository = cartRepository;
        this.productServiceClient = productServiceClient;
        this.promotionServiceClient = promotionServiceClient;
    }

    @Transactional
    public Cart createCart(CartRequest req) {
        Cart cart = Cart.builder()
            .memberId(req.getMemberId())
            .productId(req.getProductId())
            .qty(req.getQty())
            .cartStatus(CartStatus.IN_CART)
            .build();

        cartRepository.save(cart);

        return cart;
    }

    @Override
    public Map<Long, List<CartResponse>> getCarts(Long memberId) {

        List<Cart> carts = cartRepository.findAllByMemberIdAndCartStatus(memberId, CartStatus.IN_CART);
        List<Long> productIds = carts.stream().map(Cart::getProductId).collect(Collectors.toList());

        Map<Long, ProductDto> cartProductMap = productServiceClient.getOrderProducts(productIds);

        List<CartResponse> allCartResponse = carts.stream().map(cart -> CartResponse.create(cart, cartProductMap.get(cart.getProductId())))
            .collect(Collectors.toList());

        Map<Long, List<CartResponse>> sellerCartListMap = new HashMap<>();
        allCartResponse.forEach(cartDto -> {
            Long sellerId = cartDto.getSellerId();

            List<CartResponse> sellerCartList = sellerCartListMap.getOrDefault(sellerId, new ArrayList<>());
            if (sellerCartList.isEmpty()) {
                sellerCartListMap.put(sellerId, sellerCartList);
            }
            sellerCartList.add(cartDto);
        });

        return sellerCartListMap;
    }

    @Override
    public CartAvailableQtyResponse updateQty(CartQtyRequest req) {
        Long cartId = req.getCartId();
        Optional<Cart> cart = cartRepository.findById(cartId);

        // 장바구니를 찾을 수 없는 경우
        if (cart.isEmpty()) {
            log.error("[CartServiceImpl] 유효하지 않은 장바구니: cart_id={}", cartId);
            throw new ApiRequestException(
                ExceptionMessage.createCartNotFoundMsg(cartId)
            );
        }

        // 장바구니 상품 재고 확인
        Cart foundCart = cart.get();
        ProductStock productStock = productServiceClient.getProductStock(foundCart.getProductId());
        Integer stock = productStock.getStock();

        // 재고가 부족한 경우
        Integer updatedQty = req.getQty();
        if (stock < updatedQty) {
            log.error("[CartServiceImpl] 장바구니 상품 수량 증가 실패 (상품 재고 부족): product_id={}, stock={}, updatedQty={}",
                productStock.getId(), productStock.getStock(), updatedQty);

            // 주문 가능한 최대 상품 개수로 업데이트
            foundCart.changeQty(stock);
            cartRepository.save(foundCart);


            throw new ApiRequestException(
                ExceptionMessage.createProductOutOfStockMsg(stock),
                new CartAvailableQtyResponse(cartId, stock)
            );
        }

        foundCart.changeQty(updatedQty);
        cartRepository.save(foundCart);

        return new CartAvailableQtyResponse(cartId, updatedQty);
    }
}
