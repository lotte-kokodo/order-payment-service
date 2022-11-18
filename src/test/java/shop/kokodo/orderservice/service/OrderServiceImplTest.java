package shop.kokodo.orderservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import groovy.util.logging.Slf4j;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.kokodo.orderservice.dto.request.CartOrderDto;
import shop.kokodo.orderservice.dto.request.SingleProductOrderDto;
import shop.kokodo.orderservice.entity.Cart;
import shop.kokodo.orderservice.entity.Order;
import shop.kokodo.orderservice.entity.enums.status.CartStatus;
import shop.kokodo.orderservice.entity.enums.status.OrderStatus;
import shop.kokodo.orderservice.feign.client.MemberServiceClient;
import shop.kokodo.orderservice.feign.client.ProductServiceClient;
import shop.kokodo.orderservice.feign.client.PromotionServiceClient;
import shop.kokodo.orderservice.feign.response.OrderMemberDto;
import shop.kokodo.orderservice.feign.response.OrderProductDto;
import shop.kokodo.orderservice.feign.response.RateCouponDto;
import shop.kokodo.orderservice.kafka.KafkaProducer;
import shop.kokodo.orderservice.kafka.dto.CouponNameDto;
import shop.kokodo.orderservice.repository.interfaces.CartRepository;
import shop.kokodo.orderservice.repository.interfaces.OrderRepository;
import shop.kokodo.orderservice.service.utils.ProductPriceCalculator;


@ExtendWith(MockitoExtension.class)
@DisplayName("[주문] Service")
@Slf4j
class OrderServiceImplTest {

    @InjectMocks
    OrderServiceImpl orderService;

    @InjectMocks
    ProductPriceCalculator productPriceCalculator;

    @Mock
    OrderRepository orderRepository;
    @Mock
    CartRepository cartRepository;

    @Mock
    ProductServiceClient productServiceClient;
    @Mock
    MemberServiceClient memberServiceClient;

    @Mock
    PromotionServiceClient promotionServiceClient;

    @Mock
    KafkaProducer kafkaProducer;


    @Test
    @DisplayName("(단일상품) 유효한 상품 아이디가 들어갔을 때 모든 값이 채워진 주문 객체 리턴")
    void SingleProduct_Input_ValidProductId_Output_OrderObject() {
        // given
        Long memberId = 1L;
        Long productId = 200L;
        Long sellerId = 1L;
        Integer qty = 15;
        Long rateCouponId = 1L;
        Long fixCouponId = 1L;

        Integer price = 5000;
        Integer totalPrice = price*qty;

        String address = "서울특별시 서초구 서초동 1327-33";
        String name = "Nayeon Kwon";


        // Feign Product
        OrderProductDto orderProductDto = new OrderProductDto(productId, price, sellerId);
        when(productServiceClient.getSingleOrderProduct(productId))
            .thenReturn(orderProductDto);

        // Feign Member
        OrderMemberDto orderMemberDto = new OrderMemberDto(address, name);
        when(memberServiceClient.getOrderMember(memberId))
            .thenReturn(orderMemberDto);

        when(orderRepository.save(any(Order.class))).thenReturn(any(Order.class));

        // when
        Order result = orderService.orderSingleProduct(new SingleProductOrderDto(memberId, productId, sellerId, qty, rateCouponId, fixCouponId));

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
        Long sellerId = 1L;

        List<Long> productIds = Arrays.asList(1L, 2L, 3L);
        List<Integer> prices = Arrays.asList(5000, 10000, 15000);
        List<Integer> quantities = Arrays.asList(1, 2, 3);

        List<Long> cartIds = Arrays.asList(1L, 2L, 3L);
        List<Long> rateCouponIds = Arrays.asList(1L, 2L, 3L);
        List<Long> fixCouponIds = Arrays.asList(1L, 2L, 3L);

        CartOrderDto cartOrderDto = new CartOrderDto(memberId, cartIds, rateCouponIds, fixCouponIds);

        Integer totalPrice = 0;
        for (int i=0; i<prices.size(); i++) {
            totalPrice += prices.get(i)*quantities.get(i);
        }

        List<Cart> carts = new ArrayList<>();
        for (int i=0; i<cartIds.size(); i++) {
            carts.add(Cart.create(cartIds.get(i), memberId, productIds.get(i), quantities.get(i), CartStatus.IN_CART));
        }

        List<OrderProductDto> productPrices = new ArrayList<>();
        for (int i=0; i<productIds.size(); i++) {
            productPrices.add(new OrderProductDto(productIds.get(i), prices.get(i), sellerId));
        }

        String address = "서울특별시 서초구 서초동 1327-33";
        String name = "NaYeon Kwon";
        OrderMemberDto memberOrderDto = new OrderMemberDto(address, name);

        when(cartRepository.findByIdIn(cartIds)).thenReturn(carts);
        when(memberServiceClient.getOrderMember(memberId)).thenReturn(memberOrderDto);


        // when
        Order result = orderService.orderCartProducts(cartOrderDto);

        // then
        assertEquals(result.getTotalPrice(), totalPrice);
        assertEquals(result.getOrderStatus(), OrderStatus.ORDER_SUCCESS);

        System.out.println(String.format("[주문상태] %s", OrderStatus.ORDER_SUCCESS));
    }

    @Nested
    @DisplayName("주문 시 쿠폰상태 업데이트 로직 테스트")
    class OrderCouponStatusTest {

        Long memberId = 1L;

        Long rateCouponAId = 1L;
        Long rateCouponBId = 2L;
        String rateCouponAName = "RATE COUPON A";
        String rateCouponBName = "RATE COUPON B";
        RateCouponDto rateCouponA = new RateCouponDto(rateCouponAId, "RATE COUPON A");
        RateCouponDto rateCouponB = new RateCouponDto(rateCouponBId, "RATE COUPON B");
        List<Long> rateCouponIds = List.of(rateCouponAId, rateCouponBId);

        Long fixCouponAId = 1L;
        Long fixCouponBId = 2L;
        List<Long> fixCouponIds = List.of(1L, 2L);

        Map<Long, RateCouponDto> emptyRateCouponDtoMap = new HashMap<>();
        Map<Long, RateCouponDto> rateCouponDtoMap = new HashMap<>() {{
           put(rateCouponAId, rateCouponA);
           put(rateCouponBId, rateCouponB);
        }};
        Map<Long, RateCouponDto> rateCouponADtoMap = new HashMap<>() {{
           put(rateCouponAId, rateCouponA);
        }};

        @Test
        @DisplayName("비율&정책쿠폰이 적용되지 않은 경우")
        void OrderCouponStatus_RateCouponIdAndFixCouponIdNull() {
            // when
            CouponNameDto result = orderService.getValidCouponNameDto(memberId, null, null, emptyRateCouponDtoMap);

            // then
            assertNull(result);
        }


        @Test
        @DisplayName("비율할인쿠폰만 적용된 경우")
        void OrderCouponStatus_RateCouponNotNull() {
            // when
            CouponNameDto result = orderService.getValidCouponNameDto(memberId, rateCouponAId, null, rateCouponADtoMap);
            // then
            assertEquals(result.getRateCouponNames().get(0), rateCouponAName);
        }


        @Test
        @DisplayName("고정할인쿠폰만 적용된 경우")
        void OrderCouponStatus_FixCouponNotNull() {
            // when
            CouponNameDto result = orderService.getValidCouponNameDto(memberId, null, fixCouponAId, rateCouponADtoMap);
            // then
            assertEquals(result.getFixCouponIdList().size(), 1);
            assertEquals(result.getFixCouponIdList().get(0), fixCouponAId);
        }

        @Test
        @DisplayName("비율&정책쿠폰리스트가 모두 비어있는 경우 (쿠폰이 하나라도 적용되지 않은 경우)")
        void OrderCouponStatus_RateCouponIdListAndFixCouponIdListNull() {
            // when
            CouponNameDto result = orderService.getValidCouponNameDto(memberId, new ArrayList<>(), new ArrayList<>(), emptyRateCouponDtoMap);

            // then
            assertNull(result);
        }

        @Test
        @DisplayName("비율쿠폰리스트만 있는 경우 (쿠폰이 하나라도 적용된 경우)")
        void OrderCouponStatus_RateCouponIdListNotNull() {
            // when
            CouponNameDto result = orderService.getValidCouponNameDto(memberId, rateCouponIds, new ArrayList<>(), rateCouponDtoMap);

            // then
            assertEquals(result.getRateCouponNames().size(), 2);
            assertEquals(result.getRateCouponNames().get(0), rateCouponAName);
            assertEquals(result.getRateCouponNames().get(1), rateCouponBName);
        }

        @Test
        @DisplayName("정책쿠폰리스트만 있는 경우 (쿠폰이 하나라도 적용된 경우)")
        void OrderCouponStatus_FixCouponIdListNotNull() {
            // when
            CouponNameDto result = orderService.getValidCouponNameDto(memberId, new ArrayList<>(), fixCouponIds, emptyRateCouponDtoMap);
            // then
            assertEquals(result.getFixCouponIdList(), fixCouponIds);
            assertEquals(result.getRateCouponNames().size(), 0);
        }

    }


}
