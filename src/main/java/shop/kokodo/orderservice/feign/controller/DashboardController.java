package shop.kokodo.orderservice.feign.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
     * 오늘 주문건수 조회 API
     */
    @GetMapping("/{sellerId}/dashboard/todayCount")
    public ResponseEntity<Long> getTodayOrderCount(@PathVariable Long sellerId) {
        Long todayOrderCount = dashboardService.findTodayOrderCount(sellerId);
        return ResponseEntity.ok(todayOrderCount);
    }

    /**
     * 월별 주문건수 조회 API
     */

}
