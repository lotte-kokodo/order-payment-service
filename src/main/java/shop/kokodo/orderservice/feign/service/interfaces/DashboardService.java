package shop.kokodo.orderservice.feign.service.interfaces;

import java.util.Map;
import shop.kokodo.orderservice.feign.response.MonthlyOrderCountDto;

public interface DashboardService {

    Long getTodayOrderCount(Long sellerId);

    Long[]  getMonthlyOrderCount(Long sellerId);
}
