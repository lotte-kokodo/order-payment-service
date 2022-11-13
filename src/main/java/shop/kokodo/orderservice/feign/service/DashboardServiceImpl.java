package shop.kokodo.orderservice.feign.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import shop.kokodo.orderservice.entity.Order;
import shop.kokodo.orderservice.entity.OrderProduct;
import shop.kokodo.orderservice.feign.client.ProductServiceClient;
import shop.kokodo.orderservice.feign.repository.DashboardRepository;
import shop.kokodo.orderservice.feign.service.interfaces.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;
    private final ProductServiceClient productServiceClient;

    public DashboardServiceImpl(
        DashboardRepository dashboardRepository,
        ProductServiceClient productServiceClient) {
        this.dashboardRepository = dashboardRepository;
        this.productServiceClient = productServiceClient;
    }

    @Override
    public Long findTodayOrderCount(Long sellerId) {
        // 당일 주문 조회
        List<Order> todayOrders = dashboardRepository.findTodayOrder();

        // 당일 주문상품 리스트 생성
        List<OrderProduct> todayOrderProducts = new ArrayList<>();
        todayOrders.stream().map(Order::getOrderProducts).forEach(todayOrderProducts::addAll);

        // 상품아이디 리스트
        List<Long> productIds = todayOrderProducts.stream().map(OrderProduct::getProductId)
            .collect(Collectors.toList());

        Long todayOrderCount = productServiceClient.getTodayOrderCount(sellerId, productIds);

        return todayOrderCount;
    }
}
