package shop.kokodo.orderpaymentservice.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import shop.kokodo.orderpaymentservice.dto.response.dto.MemberResponse;
import shop.kokodo.orderpaymentservice.dto.response.dto.ProductResponse;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderRepository;


@ExtendWith(MockitoExtension.class)
@DisplayName("[주문] Service")
class OrderServiceImplTest {

    @InjectMocks
    OrderServiceImpl orderService;


    @Mock
    ProductServiceClient productServiceClient;

    @Mock
    MemberServiceClient memberServiceClient;

    @Mock
    OrderRepository orderRepository;

    @Mock
    CartRepository cartRepository;

    @Spy
    static ModelMapper modelMapper = new ModelMapper();


    @Nested
    @DisplayName("성공 로직 테스트 케이스")
    class SuccessCase {

        @Test
        @DisplayName("단일상품 주문")
        void orderSingleProduct() {
            // given
            Long memberId = 1L;
            Long productId = 1L;
            Integer qty = 250;

            // when
            when(productServiceClient.getProduct(productId))
                .thenReturn(productResponse);
            when(memberServiceClient.getMember(memberId))
                .thenReturn(memberResponse);
            when(orderRepository.save(any(Order.class))).thenReturn(singleProductOrder);

            Long orderId = orderService.orderSingleProduct(memberId, productId, qty);

            // then
            assertNull(orderId);
        }

        @Test
        @DisplayName("장바구니상품 주문")
        void orderCartProducts() {
            // given
            Long memberId = 1L;
            List<Long> cartIds = List.of(1L, 2L, 3L, 4L);

            // when
            when(cartRepository.findByIdIn(any())).thenReturn(carts);
            when(productServiceClient.getProducts(any())).thenReturn(productResponses);
            when(memberServiceClient.getMember(any())).thenReturn(memberResponse);

            Long orderId = orderService.orderCartProducts(memberId, cartIds);

            // then
            assertNull(orderId);
        }

    }

    @Nested
    @DisplayName("실패 로직 테스트 케이스")
    class FailureCase {

    }


    public static List<OrderProduct> orderProducts = new ArrayList<>();
    public static OrderProduct orderProduct;

    public static String memberName;
    public static String memberAddress;

    public static Integer unitPrice;
    public static Integer qty;
    public static Integer oneOrderProductTotalPrice;
    public static Integer cartOrderTotalPrice;

    public static Order singleProductOrder;
    public static Order cartProductOrder;

    public static List<Cart> carts = new ArrayList<>();


    public static ProductResponse productResponse;
    public static List<Long> productIds = new ArrayList<>();
    public static List<ProductResponse> productResponses = new ArrayList<>();

    public static MemberResponse memberResponse;

    @BeforeAll
    static void setUp() {

        for (long i=0; i<3; i++) {
            OrderProduct orderProduct = OrderProduct.builder()
                .productId(i)
                .memberId(i)
                .qty(Long.valueOf(i).intValue()*100)
                .unitPrice(Long.valueOf(i).intValue()*25000)
                .build();

            orderProducts.add(orderProduct);
        }
        orderProduct = orderProducts.get(0);

        unitPrice = 35000;
        qty = 200;
        oneOrderProductTotalPrice = unitPrice*qty;

        memberName = "NaYeon Kwon";
        memberAddress = "서울특별시 강남구 가로수길 43";


        singleProductOrder = Order.builder()
            .deliveryMemberName(memberName)
            .deliveryMemberAddress(memberAddress)
            .totalPrice(oneOrderProductTotalPrice)
            .orderDate(LocalDateTime.now())
            .orderProducts(List.of(orderProduct))
            .build();

        cartProductOrder = Order.builder()
            .deliveryMemberName(memberName)
            .deliveryMemberAddress(memberAddress)
            .totalPrice(oneOrderProductTotalPrice)
            .orderDate(LocalDateTime.now())
            .orderProducts(orderProducts)
            .build();


        for (long i=0; i<3; i++) {
            Cart cart = Cart.builder()
                .memberId(i)
                .productId(i)
                .qty(Long.valueOf(i).intValue()*100)
                .build();

            carts.add(cart);
        }

        productIds = List.of(1L, 2L, 3L, 4L);
        for (int i=0; i<productIds.size(); i++) {
            productResponses.add(new ProductResponse(productIds.get(i),
                Long.valueOf(i).intValue()*25000));
        }
        productResponse = productResponses.get(0);

        memberResponse = new MemberResponse("NaYeon Kwon",
            "서울특별시 강남구 가로수길 43");

    }

}