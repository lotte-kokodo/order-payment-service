package shop.kokodo.orderservice.feign.service.interfaces;

import shop.kokodo.orderservice.feign.response.OrderCountResponseDto;
import shop.kokodo.orderservice.feign.response.ProductIdResponseDto;

public interface DashboardService {

    OrderCountResponseDto getOrderCount(Long sellerId);

    Long[]  getMonthlyOrderCount(Long sellerId);
}
