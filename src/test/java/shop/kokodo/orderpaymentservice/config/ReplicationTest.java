package shop.kokodo.orderpaymentservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import shop.kokodo.orderpaymentservice.service.interfaces.CartService;
import shop.kokodo.orderpaymentservice.service.interfaces.OrderService;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReplicationTest {

    private final CartService cartService;

    @Autowired
    public ReplicationTest(CartService cartService) {
        this.cartService = cartService;
    }

    Long memberId;
    Long productId ;
    Integer qty;

    @BeforeEach
    public void setUp(){
        memberId = 1L;
        productId = 1L;
        qty = 350;
    }

    @Test
    @DisplayName("cart DB write")
    public void cart_write(){

        cartService.createCart(memberId,productId,qty);

    }

    @Test
    @DisplayName("cart DB read")
    public void cart_read(){

        cartService.createCart(memberId,productId,qty);
        cartService.getCartProducts(memberId);
        cartService.getCartProducts(memberId);
        cartService.getCartProducts(memberId);
    }
}
