package shop.kokodo.orderservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.kokodo.orderservice.dto.request.CartDto;
import shop.kokodo.orderservice.dto.response.CartAvailableQtyDto;
import shop.kokodo.orderservice.dto.request.CartQtyDto;
import shop.kokodo.orderservice.entity.Cart;
import shop.kokodo.orderservice.entity.enums.status.CartStatus;
import shop.kokodo.orderservice.exception.api.ApiRequestException;
import shop.kokodo.orderservice.feign.client.ProductServiceClient;
import shop.kokodo.orderservice.feign.response.CartProductDto;
import shop.kokodo.orderservice.feign.response.ProductStockDto;
import shop.kokodo.orderservice.message.ExceptionMessage;
import shop.kokodo.orderservice.repository.interfaces.CartRepository;
import shop.kokodo.orderservice.service.interfaces.CartService;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final ProductServiceClient productServiceClient;

    private final CircuitBreakerFactory circuitBreakerFactory;


    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
        ProductServiceClient productServiceClient,
        CircuitBreakerFactory circuitBreakerFactory) {
        this.cartRepository = cartRepository;
        this.productServiceClient = productServiceClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Transactional
    public Cart createCart(CartDto req) {
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
    public Map<Long, List<shop.kokodo.orderservice.dto.response.CartDto>> getCarts(Long memberId) {

        List<Cart> carts = cartRepository.findAllByMemberIdAndCartStatus(memberId, CartStatus.IN_CART);
        List<Long> productIds = carts.stream().map(Cart::getProductId).collect(Collectors.toList());

        Map<Long, CartProductDto> cartProductMap = runCircuitBreaker("getProductOfCartCB",
            () -> productServiceClient.getOrderProducts(productIds), throwable -> new HashMap<Long, CartProductDto>());

        List<shop.kokodo.orderservice.dto.response.CartDto> allCartDto = carts.stream().map(cart -> shop.kokodo.orderservice.dto.response.CartDto.create(cart, cartProductMap.get(cart.getProductId())))
            .collect(Collectors.toList());

        Map<Long, List<shop.kokodo.orderservice.dto.response.CartDto>> sellerCartListMap = new HashMap<>();
        allCartDto.forEach(cartDto -> {
            Long sellerId = cartDto.getSellerId();

            List<shop.kokodo.orderservice.dto.response.CartDto> sellerCartList = sellerCartListMap.getOrDefault(sellerId, new ArrayList<>());
            if (sellerCartList.isEmpty()) {
                sellerCartListMap.put(sellerId, sellerCartList);
            }
            sellerCartList.add(cartDto);
        });

        return sellerCartListMap;
    }

    @Override
    public CartAvailableQtyDto updateQty(CartQtyDto req) {
        Long cartId = req.getCartId();
        Cart cart = cartRepository.findById(cartId).orElseThrow(
            () -> {
                log.error("[CartServiceImpl] 유효하지 않은 장바구니: cart_id={}", cartId);
                throw new ApiRequestException(
                    ExceptionMessage.createCartNotFoundMsg(cartId)
                );
            }
        );

        // 장바구니 상품 재고 확인
        Long productId = cart.getProductId();
        ProductStockDto productStock = runCircuitBreaker("getProductStockCB",
            () -> productServiceClient.getProductStock(productId), throwable -> new ProductStockDto(productId, -1));

        Integer stock = productStock.getStock();

        // Product Service 와 통신이 되지 않는 경우
        if (stock == -1) {
            log.error("[CartServiceImpl] 상품 서버 통신 오류: product_id={}", productId);
            throw new ApiRequestException(ExceptionMessage.CANNOT_BE_ATTEMPTED_COMMUNICATION);
        }

        // 재고가 부족한 경우
        Integer updatedQty = req.getQty();
        if (stock < updatedQty) {
            log.error("[CartServiceImpl] 장바구니 상품 수량 증가 실패 (상품 재고 부족): product_id={}, stock={}, updatedQty={}",
                productStock.getId(), productStock.getStock(), updatedQty);

            // 주문 가능한 최대 상품 개수로 업데이트
            cart.changeQty(stock);
            cartRepository.save(cart);

            throw new ApiRequestException(
                ExceptionMessage.createProductOutOfStockMsg(stock),
                new CartAvailableQtyDto(cartId, stock)
            );
        }

        cart.changeQty(updatedQty);
        cartRepository.save(cart);

        return new CartAvailableQtyDto(cartId, updatedQty);
    }

    private <T> T runCircuitBreaker(String id, Supplier<T> toRun, Function<Throwable, T> fallback) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(id);
        return circuitBreaker.run(toRun, fallback);
    }
}
