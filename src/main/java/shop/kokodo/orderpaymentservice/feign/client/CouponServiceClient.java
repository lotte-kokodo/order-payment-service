package shop.kokodo.orderpaymentservice.feign.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.CouponFixDiscount;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.CouponRateDiscount;

@FeignClient(name = "coupon-service") // application name
public interface CouponServiceClient {

    // 비율할인정책
    @GetMapping("")
    Map<Long, CouponRateDiscount> getCouponRateDiscounts(List<Long> productIds);

    // 고정할인정책
    @GetMapping("")
    Map<Long, CouponFixDiscount> getCouponFixDiscounts(List<Long> productIds);

}
