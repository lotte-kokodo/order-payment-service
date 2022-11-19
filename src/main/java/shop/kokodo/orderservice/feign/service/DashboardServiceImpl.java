package shop.kokodo.orderservice.feign.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import shop.kokodo.orderservice.feign.client.ProductServiceClient;
import shop.kokodo.orderservice.feign.dto.ProductCountDto;
import shop.kokodo.orderservice.feign.repository.DashboardRepository;
import shop.kokodo.orderservice.feign.request.OrderCountRequestDto;
import shop.kokodo.orderservice.feign.response.MonthCountDto;
import shop.kokodo.orderservice.feign.response.OrderCountResponseDto;
import shop.kokodo.orderservice.feign.response.ProductIdResponseDto;
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
    public OrderCountResponseDto getOrderCount(Long sellerId) {
        // 당일 주문상품 아이디리스트 조회
        List<ProductCountDto> todayOrderProductCounts = dashboardRepository.findTodayOrderProductCountMap(ProductCountDto.class);

        LocalDateTime start = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(0,0,0)); // 어제 00:00:00
        LocalDateTime end = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(23,59,59)); // 어제 23:59:59
        List<ProductCountDto> yesterdayOrderProductCounts = dashboardRepository.findYesterdayOrderProductCountMap(start, end, ProductCountDto.class);

        Map<Long, Integer> todayOrderProductCountMap = todayOrderProductCounts.stream().collect(Collectors.toMap(ProductCountDto::getId, ProductCountDto::getCount));
        Map<Long, Integer> yesterdayOrderProductCountMap = yesterdayOrderProductCounts.stream().collect(Collectors.toMap(ProductCountDto::getId, ProductCountDto::getCount));

        List<Long> todayOrderProductIds = todayOrderProductCounts.stream().map(ProductCountDto::getId).collect(Collectors.toList());
        List<Long> yesterdayOrderProductIds = yesterdayOrderProductCounts.stream().map(ProductCountDto::getId).collect(Collectors.toList());

        ProductIdResponseDto productIdResponseDto = productServiceClient.getOrderCount(new OrderCountRequestDto(sellerId, todayOrderProductIds, yesterdayOrderProductIds));

        Integer todayOrderCount = productIdResponseDto.getTodayOrderProductIds().stream()
            .map(todayOrderProductCountMap::get).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        Integer yesterdayOrderCount = productIdResponseDto.getYesterdayOrderProductIds().stream()
            .map(yesterdayOrderProductCountMap::get).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();

        return new OrderCountResponseDto(todayOrderCount, yesterdayOrderCount);
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
