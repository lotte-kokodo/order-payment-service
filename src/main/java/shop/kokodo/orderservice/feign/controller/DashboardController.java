package shop.kokodo.orderservice.feign.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.kokodo.orderservice.feign.response.OrderCountResponseDto;
import shop.kokodo.orderservice.feign.response.ProductIdResponseDto;
import shop.kokodo.orderservice.feign.service.interfaces.DashboardService;

@RestController
@RequestMapping("/orders/feign/seller")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
        DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 오늘, 어제 주문건수 조회 API
     */
    @GetMapping("/{sellerId}/dashboard/count")
    public ResponseEntity<OrderCountResponseDto> getTodayOrderCount(@PathVariable Long sellerId) {
        OrderCountResponseDto orderCount = dashboardService.getOrderCount(sellerId);
        return ResponseEntity.ok(orderCount);
    }

    /**
     * 월별 주문건수 조회 API
     */
    @GetMapping("/{sellerId}/productId")
    public ResponseEntity<Long[]> getMonthlyOrderCount(@PathVariable Long sellerId) {
        Long[] monthlyOrderCount = dashboardService.getMonthlyOrderCount(sellerId);
        return ResponseEntity.ok(monthlyOrderCount);
    }
}
