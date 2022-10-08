package shop.kokodo.orderpaymentservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import groovy.util.logging.Slf4j;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;
import shop.kokodo.orderpaymentservice.entity.enums.order.OrderStatus;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.MemberAddress;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductPrice;
import shop.kokodo.orderpaymentservice.messagequeue.KafkaProducer;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderRepository;
import shop.kokodo.orderpaymentservice.feign.client.MemberServiceClient;
import shop.kokodo.orderpaymentservice.feign.client.ProductServiceClient;


@ExtendWith(MockitoExtension.class)
@DisplayName("[주문] Service")
@Slf4j
class OrderServiceImplTest {

    @InjectMocks
    OrderServiceImpl orderService;

    @Spy
    ModelMapper modelMapper;

    @Mock
    OrderRepository orderRepository;
    @Mock
    CartRepository cartRepository;

    @Mock
    ProductServiceClient productServiceClient;
    @Mock
    MemberServiceClient memberServiceClient;

    @Mock
    KafkaProducer kafkaProducer;


    @Nested
    @DisplayName("성공 로직 테스트 케이스")
    class SuccessCase {

        @Test
        @DisplayName("(단일상품) 유효한 상품 아이디가 들어갔을 때 모든 값이 채워진 주문 객체 리턴")
        void SingleProduct_Input_ValidProductId_Output_OrderObject() {
            // given
            Long memberId = 1L;
            Long productId = 200L;
            Integer qty = 15;
            Long couponId = 1L;

            Integer price = 5000;

            String address = "서울특별시 서초구 서초동 1327-33";


            // Feign Product
            FeignResponse.ProductPrice productPrice = new FeignResponse.ProductPrice(productId, price);
            when(productServiceClient.getProduct(productId))
                .thenReturn(productPrice);

            // Feign Member
            MemberAddress memberAddress = new MemberAddress(address);
            when(memberServiceClient.getMemberAddress(memberId))
                .thenReturn(memberAddress);

            OrderProduct orderProduct = OrderProduct.builder()
                .memberId(memberId)
                .productId(productId)
                .qty(qty)
                .unitPrice(price)
                .build();

            Order order = Order.builder()
                .deliveryMemberAddress(memberAddress.getAddress())
                .totalPrice(price*qty)
                .orderDate(LocalDateTime.now())
                .orderProducts(List.of(orderProduct))
                .build();

            when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

            // when
            Order result = orderService.orderSingleProduct(memberId, productId, qty, couponId);

            // then
            assertEquals(result.getTotalPrice(), 75000);
            assertEquals(result.getOrderStatus(), OrderStatus.ORDER_SUCCESS);

            System.out.println(String.format("[주문상태] %s", OrderStatus.ORDER_SUCCESS));
        }

        @Test
        @DisplayName("(장바구니상품) 유효한 상품 아이디가 들어갔을 때 모든 값이 채워진 주문 객체 리턴")
        void CartProduct_Input_ValidProductId_Output_OrderObject() {
            // given

            Long memberId = 1L;

            List<Long> productIds = Arrays.asList(1L, 2L, 3L);
            List<Integer> prices = Arrays.asList(5000, 10000, 15000);
            List<Integer> quantities = Arrays.asList(1, 2, 3);

            List<Long> cartIds = Arrays.asList(1L, 2L, 3L);
            List<Long> couponIds = Arrays.asList(1L, 2L, 3L);

            Integer totalPrice = 0;
            for (int i=0; i<prices.size(); i++) {
                totalPrice += prices.get(i)*quantities.get(i);
            }

            List<Cart> carts = new ArrayList<>();
            for (int i=0; i<cartIds.size(); i++) {
                carts.add(new Cart(cartIds.get(i), memberId, productIds.get(i), quantities.get(i), prices.get(i)));
            }

            List<FeignResponse.ProductPrice> productPrices = new ArrayList<>();
            for (int i=0; i<productIds.size(); i++) {
                productPrices.add(new ProductPrice(productIds.get(i), prices.get(i)));
            }

            String address = "서울특별시 서초구 서초동 1327-33";
            MemberAddress memberAddress
                = new MemberAddress(address);

            when(cartRepository.findByIdIn(cartIds)).thenReturn(carts);
            when(memberServiceClient.getMemberAddress(memberId)).thenReturn(memberAddress);


            // when
            Order result = orderService.orderCartProducts(memberId, cartIds, couponIds);

            // then
            assertEquals(result.getTotalPrice(), totalPrice);
            assertEquals(result.getOrderStatus(), OrderStatus.ORDER_SUCCESS);

            System.out.println(String.format("[주문상태] %s", OrderStatus.ORDER_SUCCESS));
        }

    }

    @Nested
    @DisplayName("실패 로직 테스트 케이스")
    class FailureCase {



    }


}