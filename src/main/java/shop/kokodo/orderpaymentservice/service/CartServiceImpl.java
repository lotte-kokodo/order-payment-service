package shop.kokodo.orderpaymentservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.service.interfaces.CartService;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Long createCart(Long memberId, Long productId, Integer qty) {
        Cart cart = Cart.builder()
            .memberId(memberId)
            .productId(productId)
            .qty(qty)
            .build();

        cartRepository.save(cart);

        return cart.getId();
    }
}
