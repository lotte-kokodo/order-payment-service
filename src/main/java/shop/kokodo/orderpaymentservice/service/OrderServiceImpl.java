package shop.kokodo.orderpaymentservice.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.kokodo.orderpaymentservice.dto.response.dto.MemberResponse;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderInformationDto;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderResponse;
import shop.kokodo.orderpaymentservice.entity.*;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderProductRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.ProductRepository;
import shop.kokodo.orderpaymentservice.service.interfaces.OrderService;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final ModelMapper modelMapper;

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    /* feignclient 전 productRepository사용을 위한 repository */
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    // FeignClient
//    private final MemberServiceClient memberServiceClient;
//    private final ProductServiceClient productServiceClient;

    @Autowired
    public OrderServiceImpl(
            ModelMapper modelMapper,
            OrderRepository orderRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            OrderProductRepository orderProductRepository) {

        this.modelMapper = modelMapper;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @Transactional
    public Long orderSingleProduct(Long memberId, Long productId, Integer qty) {

        // TODO: FeignClient 통신 테스트
        // 상품 가격
//        ProductResponse productResponse = productServiceClient.getProduct(productId);
//        Integer unitPrice = productResponse.getUnitPrice();
        Integer unitPrice = 10000;

        // 주문 상품 생성
        OrderProduct orderProduct = OrderProduct.builder()
                .memberId(memberId)
                .productId(productId)
                .qty(qty)
                .unitPrice(unitPrice)
                .build();


        // TODO: FeignClient 통신 테스트
        // 사용자 이름, 주소
//        MemberResponse memberResponse = memberServiceClient.getMember(memberId);
        MemberResponse memberResponse = new MemberResponse("NaYeon Kwon",
                "서울특별시 강남구 가로수길 43");

        // 주문 생성
        Order order = Order.builder()
                .deliveryMemberName(memberResponse.getMemberName())
                .deliveryMemberAddress(memberResponse.getMemberAddress())
                .totalPrice(unitPrice * qty)
                .orderDate(LocalDateTime.now())
                .orderProducts(List.of(orderProduct))
                .build();

        orderRepository.save(order);

        return order.getId();
    }

    @Transactional
    public Long orderCartProducts(Long memberId, List<Long> cartIds) {
        // '장바구니상품' 조회
        List<Cart> carts = cartRepository.findByIdIn(cartIds);

        // 주문 상품 생성
        List<OrderProduct> orderProducts = carts.stream()
                .map(cart -> modelMapper.map(cart, OrderProduct.class))
                .collect(Collectors.toList());

        // 단일상품가격(unitPrice) 세팅
        List<Long> productIds = carts.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toList());
        ;

//        List<ProductResponse> productResponses = productServiceClient.getProducts(productIds);
//        modelMapper.map(productResponses, orderProducts);

        // 주문 총 가격 계산
        Integer totalPrice = orderProducts.stream()
                .map(OrderProduct::getUnitPrice)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        // 사용자 이름, 주소
//        MemberResponse memberResponse = memberServiceClient.getMember(memberId);
        MemberResponse memberResponse = new MemberResponse("NaYeon Kwon",
                "서울특별시 강남구 가로수길 43");

        // 주문 생성
        Order order = Order.builder()
                .deliveryMemberName(memberResponse.getMemberName())
                .deliveryMemberAddress(memberResponse.getMemberAddress())
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .orderProducts(orderProducts)
                .build();

        orderRepository.save(order);

        return order.getId();
    }

    /**
     * memberId를 받아 OrderInformationDto를 return하는 함수
     * memberId -> OrderProductList
     * OrderProductList.id -> ProductList
     * ProductList.id -> Product
     * 
     * @param memberId
     * @return List<OrderInformationDto>
     */
    @Override
    public List<OrderInformationDto> getOrderList(Long memberId) {
        // memberId로 OrderProduct들 갖고오기
        // OrderProduct들로 productId들 갖고오기
        List<OrderProduct> orderProductList = orderProductRepository.findAllByMemberId(memberId);
        log.info("orderProductList -> " + orderProductList.toString());
        // 값을 받아 orderId를 Map형태로 저장
        Map<Long, Long> orderIdMap = new HashMap<>();


        List<Long> orderIdList =
                orderProductList.stream()
                        .map(OrderProduct::getOrder)
                        .map(Order::getId)
                        .collect(Collectors.toList());
        log.info("orderIdList -> " + orderIdList.toString());

        for (Long orderId : orderIdList) {
            if (!orderIdMap.containsKey(orderId)) {
                orderIdMap.put(orderId, 1L);
            } else {
                orderIdMap.replace(orderId, orderIdMap.get(orderId) + 1L);
            }
        }
        log.info("orderIdMap -> " + orderIdMap.toString());
        // productId들로 Product들 갖고오기
        // Feignclient 사용 대신 test용 내장 table 사용
        List<Long> productDistinctIdList =
                orderProductList.stream()
                        .map(OrderProduct::getProductId)
                        .distinct()
                        .collect(Collectors.toList());
        log.info("productDistinctIdList -> " + productDistinctIdList.toString());
        Long[] idarray = productDistinctIdList.toArray(new Long[productDistinctIdList.size()]);
        log.info("idarray : " + idarray.toString());
        List<Product> productList = productRepository.findAllByIdIn(idarray);
        log.info("productList -> " + productList.toString());
        // Product들을 Response와 합쳐서 보내기
        List<OrderInformationDto> orderInformationDtoList = new ArrayList<>();
        for (OrderProduct orderProduct : orderProductList) {
            //OrderProduct에서 가져오는 부분
            Long orderProductId = orderProduct.getId();

            //Order에서 가져오는 부분
            Order order = orderProduct.getOrder();
            //주문번호
            Long orderId = order.getId();
            //주문시간
            LocalDateTime orderDateTime = order.getOrderDate();
            //결제금액
            Integer price = order.getTotalPrice();
            //주문상태
            OrderStatus orderStatus = order.getOrderStatus();

            //product에서 가져오는 부분
            //제목
            String name = "";
            //썸네일
            String thumbnail = "";
            for (Product product : productList) {
                log.info("프로덕트 vs 오더프로덕트 아이디 :: " + product.toString() + " *** " + orderProduct.toString());
                if (product.getId() == orderProduct.getProductId()) {
                    name = product.getDisplayName();
                    if(orderIdMap.get(product.getId()) != 1) {
                        name = name + "외 " + (orderIdMap.get(product.getId()) - 1) + "건";
                    }
                    thumbnail = product.getThumbnail();
                    break;
                }
            }

            OrderInformationDto orderInformationDto = OrderInformationDto.builder()
                    .orderId(orderId)
                    .orderProductId(orderProductId)
                    .name(name)
                    .orderStatus(orderStatus)
                    .price(price)
                    .thumbnail(thumbnail)
                    .orderDate(orderDateTime)
                    .build();
            orderInformationDtoList.add(orderInformationDto);
        }
        log.info("orderInformationDtoList -> " + orderInformationDtoList.toString());
        List<OrderInformationDto> result = new ArrayList<>();
        for(int i=0;i<orderIdList.size();i++) {
            if(orderIdList.indexOf(orderIdList.get(i)) == orderIdList.lastIndexOf(orderIdList.get(i))) {
                result.add(orderInformationDtoList.get(i));
            }
        }
        log.info("result -> " + result.toString());
        return result;
    }

    @Override
    public List<OrderResponse> getOrderDetailList(Long memberId, Long orderId) {

        return null;
    }
}
