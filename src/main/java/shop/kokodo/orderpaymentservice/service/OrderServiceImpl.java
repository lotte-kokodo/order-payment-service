package shop.kokodo.orderpaymentservice.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import shop.kokodo.orderpaymentservice.dto.response.data.OrderResponse.OrderSheet;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderDetailInformationDto;
import shop.kokodo.orderpaymentservice.dto.response.dto.OrderInformationDto;
import shop.kokodo.orderpaymentservice.entity.Cart;
import shop.kokodo.orderpaymentservice.entity.Order;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;
import shop.kokodo.orderpaymentservice.entity.enums.order.OrderStatus;
import shop.kokodo.orderpaymentservice.feign.client.MemberServiceClient;
import shop.kokodo.orderpaymentservice.feign.client.ProductServiceClient;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.MemberDeliveryInfo;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.MemberOfOrderSheet;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductOfOrderSheet;
import shop.kokodo.orderpaymentservice.messagequeue.KafkaProducer;
import shop.kokodo.orderpaymentservice.repository.interfaces.CartRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderProductRepository;
import shop.kokodo.orderpaymentservice.repository.interfaces.OrderRepository;
import shop.kokodo.orderpaymentservice.service.interfaces.OrderService;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    /* feignclient 전 productRepository사용을 위한 repository */
//    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    // FeignClient
    private final ProductServiceClient productServiceClient;
    private final MemberServiceClient memberServiceClient;

    private final KafkaProducer kafkaProducer;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            ProductServiceClient productServiceClient,
            MemberServiceClient memberServiceClient,
            KafkaProducer kafkaProducer,
//        ProductRepository productRepository,
            OrderProductRepository orderProductRepository) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.orderProductRepository = orderProductRepository;
        this.productServiceClient = productServiceClient;
        this.memberServiceClient = memberServiceClient;
        this.kafkaProducer = kafkaProducer;
//        this.productRepository = productRepository;
    }

    @Transactional
    public Order orderSingleProduct(Long memberId, Long productId, Integer qty, Long couponId) {

        // 상품 가격
        FeignResponse.ProductPrice productPrice = productServiceClient.getProduct(productId);
        Integer unitPrice = productPrice.getPrice();

        // 주문 상품 생성
        OrderProduct orderProduct = OrderProduct.builder()
                .memberId(memberId)
                .productId(productId)
                .qty(qty)
                .unitPrice(unitPrice)
                .build();

        // 사용자 이름, 주소
        MemberDeliveryInfo memberDeliveryInfo = memberServiceClient.getMemberAddress(memberId);

        // 주문 생성
        Order order = Order.builder()
                .memberId(memberId)
                .deliveryMemberAddress(memberDeliveryInfo.getAddress())
                .deliveryMemberName(memberDeliveryInfo.getName())
                .totalPrice(unitPrice * qty)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.ORDER_SUCCESS)
                .orderProducts(List.of(orderProduct))
                .build();
        orderProduct.setOrder(order);

        orderRepository.save(order);

        kafkaProducer.send("kokodo.product.de-stock", new LinkedHashMap<>() {{
            put(productId, qty);
        }});

        // TODO: 쿠폰 상태 수정 Kafka Listener 토픽 수정
        kafkaProducer.send("kokodo.coupon.status", List.of(couponId));

        return order;
    }

    @Transactional
    public Order orderCartProducts(Long memberId, List<Long> cartIds, List<Long> couponIds) {
        // '장바구니상품' 조회
        List<Cart> carts = cartRepository.findByIdIn(cartIds);

        // 주문 상품 생성
        List<OrderProduct> orderProducts = carts.stream()
                .map(OrderProduct::convertCartToOrderProduct)
                .collect(Collectors.toList());

        // 주문 총 가격 계산
        Integer totalPrice = orderProducts.stream()
                .map(orderProduct -> orderProduct.getUnitPrice() * orderProduct.getQty())
                .mapToInt(Integer::intValue)
                .sum();

        // 사용자 이름, 주소
        MemberDeliveryInfo memberDeliveryInfo = memberServiceClient.getMemberAddress(memberId);

        // 주문 생성
        Order order = Order.builder()
                .deliveryMemberAddress(memberDeliveryInfo.getAddress())
                .deliveryMemberName(memberDeliveryInfo.getName())
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.ORDER_SUCCESS)
                .memberId(memberId)
                .orderProducts(orderProducts)
                .build();

        orderProducts.forEach((orderProduct -> {
            orderProduct.setOrder(order);
        }));

        orderRepository.save(order);

        // 상품 재고 감소
        // productId (key) - qty (value) Map 생성
        Map<Long, Integer> productIdQtyMap = carts.stream()
                .collect(Collectors.toMap(Cart::getProductId, Cart::getQty));

        kafkaProducer.send("kokodo.product.de-stock", productIdQtyMap);

        // [key] "couponIds"    [value] Long List
        // map.get("couponIds")
        kafkaProducer.send("kokodo.coupon.status", couponIds);

        return order;
    }

    @Override
    public OrderSheet getOrderSheet(Long memberId, @RequestParam List<Long> productIds) {
        // 주문서 상품 정보 요청
        List<ProductOfOrderSheet> products = productServiceClient.getOrderSheetProducts(productIds);

        // 사용자 정보 요청
        MemberOfOrderSheet member = memberServiceClient.getMemberOrderInfo(memberId);

        return OrderSheet.builder()
                .productInfos(products)
                .memberInfo(member)
                .build();
    }

    @Override
    public List<OrderInformationDto> getOrderList(Long memberId) {
        //1. memberId로 OrderProduct들 갖고오기
        //2. OrderProduct들로 productId들 갖고오기
        List<Object[]> orderAndOrderProductList = orderProductRepository.findAllByMemberId(memberId);
        List<OrderProduct> orderProductList = new ArrayList<>();
        List<Order> orderList = new ArrayList<>();
        orderAndOrderProductList.stream().forEach(
                row -> {
                    orderList.add((Order) row[0]);
                    orderProductList.add((OrderProduct) row[1]);
                }
        );
        //4. Product들을 Response와 합쳐서 보내기
        List<OrderInformationDto> orderInformationDtoList = new ArrayList<>();
        for (int i = 0; i < orderProductList.size(); i++) {
            Order order = orderList.get(i);
            if (orderInformationDtoList.size() != 0) {
                if (order.getId() == orderInformationDtoList.get(orderInformationDtoList.size() - 1).getOrderId()) {
                    continue;
                }
            }
            List<Long> productIdList =
                    orderProductList.stream()
                            .filter(orderProduct -> order.getId().equals(orderProduct.getOrder().getId()))
                            .map(OrderProduct::getProductId)
                            .collect(Collectors.toList());
            //3. productId들로 Product들 갖고오기
            List<FeignResponse.Product> productList = productServiceClient.getProductList(productIdList);
            //주문번호
            Long orderId = order.getId();

            String name = "";
            String thumbnail = "";
            if (productList.size() != 0) {
                //제목
                String orderName = productList.get(0).getDisplayName();
                //썸네일
                thumbnail = productList.get(0).getThumbnail();
                if (productIdList.size() != 1) {
                    name = orderName + " 외 " + (productIdList.size() - 1) + "건";
                } else {
                    name = orderName;
                }
            }
            //주문시간
            LocalDateTime orderDate = order.getOrderDate();

            //결제금액
            int price = order.getTotalPrice();
            //주문상태
            OrderStatus orderStatus = order.getOrderStatus();

            OrderInformationDto orderInformationDto = OrderInformationDto.builder()
                    .orderId(orderId)
                    .name(name)
                    .orderStatus(orderStatus)
                    .price(price)
                    .thumbnail(thumbnail)
                    .orderDate(orderDate)
                    .build();
            orderInformationDtoList.add(orderInformationDto);
        }

        return orderInformationDtoList;
    }

    @Override
    public List<OrderDetailInformationDto> getOrderDetailList(Long memberId, Long orderId) {
        List<OrderProduct> orderProductList = orderProductRepository.findAllByIdAndMemberId(memberId, orderId);
        log.info("orderProductList : " + orderProductList.toString());

        List<Long> productIdList = orderProductList.stream()
                .map(OrderProduct::getProductId)
                .collect(Collectors.toList());
        log.info("productIdList : " + productIdList.toString());

        List<FeignResponse.Product> productList = productServiceClient.getProductList(productIdList);

        List<OrderDetailInformationDto> orderDetailInformationDtoList = new ArrayList<>();


        for (int i = 0; i < orderProductList.size(); i++) {
            OrderDetailInformationDto orderDetailInformationDto = OrderDetailInformationDto.builder()
                    .id(orderProductList.get(i).getId())
                    .name(productList.get(i).getDisplayName())
                    .orderStatus(orderProductList.get(i).getOrder().getOrderStatus())
                    .price(orderProductList.get(i).getUnitPrice())
                    .qty(orderProductList.get(i).getQty())
                    .thumbnail(productList.get(i).getThumbnail())
                    .build();
            orderDetailInformationDtoList.add(orderDetailInformationDto);
        }
        for (int i = 0; i < orderDetailInformationDtoList.size(); i++) {
            log.info("orderDetailInformationDtoList : " + orderDetailInformationDtoList.get(i).getName());
        }

        return orderDetailInformationDtoList;
    }
}
