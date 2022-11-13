package shop.kokodo.orderservice.feign.service.interfaces;

public interface DashboardService {

    Long findTodayOrderCount(Long sellerId);

}
