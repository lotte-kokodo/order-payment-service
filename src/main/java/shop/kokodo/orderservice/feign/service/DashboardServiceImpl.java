package shop.kokodo.orderservice.feign.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import shop.kokodo.orderservice.entity.OrderProduct;
import shop.kokodo.orderservice.feign.client.ProductServiceClient;
import shop.kokodo.orderservice.feign.repository.DashboardRepository;
import shop.kokodo.orderservice.feign.response.MonthCountDto;
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
    public Long getTodayOrderCount(Long sellerId) {
        // 당일 주문상품 조회
        List<OrderProduct> todayOrderProducts = dashboardRepository.findTodayOrder();

        // 상품아이디 리스트
        List<Long> productIds = todayOrderProducts.stream().map(OrderProduct::getProductId)
            .collect(Collectors.toList());

        return productServiceClient.getTodayOrderCount(sellerId, productIds);
    }

    @Override
    public Long[] getMonthlyOrderCount(Long sellerId) {
        List<Long> productIds = productServiceClient.getSellerProductIds(sellerId);
        List<MonthCountDto> monthlyOrderCount = dashboardRepository.findMonthlyOrderCount(productIds, MonthCountDto.class);

        return getMonthCountArray(monthlyOrderCount);
    }


    private Long[] getMonthCountArray(List<MonthCountDto> monthlyOrderCount) {
        // 현재 년도의 월별 주문개수 필터링
        String curYear = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        Long[] countOfMonth = new Long[12];
        Arrays.fill(countOfMonth, 0L);

        monthlyOrderCount.forEach((monthCount) -> {
            String yearMonth = monthCount.getYearMonth();
            String year = yearMonth.substring(0, 4);
            String month = yearMonth.substring(5);
            if (year.equals(curYear)) {
                countOfMonth[Integer.parseInt(month) - 1] = monthCount.getCount();
            }
        });

        return countOfMonth;
    }
}
