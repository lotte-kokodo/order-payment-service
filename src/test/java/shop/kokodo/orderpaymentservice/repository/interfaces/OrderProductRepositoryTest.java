package shop.kokodo.orderpaymentservice.repository.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;


import javax.transaction.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class OrderProductRepositoryTest {
    @Autowired
    OrderProductRepository orderProductRepository;

    @Test
    @DisplayName("memberId로 주문 조회 성공")
    void findAllByMemberId() {
        Long memberId = 1L;
        List<Object[]> orderProductList = orderProductRepository.findAllByMemberId(memberId);

        Assertions.assertEquals(orderProductList.size(), 4);
    }

    @Test
    @DisplayName("memberId와 orderId로 주문 상세 조회 성공")
    void findAllByIdAndMemberId() {
        Long memberId = 1L;
        Long orderId = 1L;

        List<OrderProduct> orderProductList = orderProductRepository.findAllByIdAndMemberId(memberId, orderId);

        System.out.println(orderProductList.toString());
        Assertions.assertEquals(orderProductList.size(), 2);
    }
}