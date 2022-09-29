package shop.kokodo.orderpaymentservice.repository.interfaces;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;

@DataJpaTest
@DisplayName("[주문] Repository")
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    static List<OrderProduct> orderProducts = new ArrayList<>();
    static String memberName;
    static String memberAddress;
    static Integer totalPrice;

    @BeforeAll
    static void setEntity() {

        for (long i=0; i<3; i++) {
            OrderProduct orderProduct = OrderProduct.builder()
                .productId(i)
                .memberId(i)
                .qty(Long.valueOf(i).intValue()*100)
                .unitPrice(Long.valueOf(i).intValue()*25000)
                .build();

            orderProducts.add(orderProduct);
        }

        memberName = "NaYeon Kwon";
        memberAddress = "서울특별시 강남구 가로수길 43";
        totalPrice = 123456789;
    }

    @Nested
    @DisplayName("성공 로직 테스트 케이스")
    class SuccessCase {

        @Test
        @DisplayName("주문 저장")
        void save() {
            // given
            Order order = Order.builder()
                .deliveryMemberName(memberName)
                .deliveryMemberAddress(memberAddress)
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .orderProducts(orderProducts)
                .build();

            // when
            orderRepository.save(order);

            // then
            assertNotNull(order.getId()); // 주문 아이디가 부여됐는지 확인
            order.getOrderProducts().forEach(orderProduct -> assertNotNull(orderProduct.getId()));
        }

    }

    @Nested
    @DisplayName("실패 로직 테스트 케이스")
    class FailureCase {

    }

}