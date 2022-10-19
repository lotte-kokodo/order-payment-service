package shop.kokodo.orderpaymentservice.feign.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.RateCoupon;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.RateDiscountPolicy;

@FeignClient(name = "promotion-service") // application name
public interface PromotionServiceClient {

    // 비율할인정책
    @GetMapping("/rate-discount/list")
    Map<Long, RateDiscountPolicy> getRateDiscountPolicy(@RequestParam("productIdList") List<Long> productIds);

    // 고정할인정책
    @GetMapping("/feign/fix-discount/status")
    Map<Long, Boolean> getFixDiscountPolicyStatusForFeign(@RequestParam List<Long> productIdList, @RequestParam List<Long> sellerIdList);

    // 비율할인쿠폰
    @GetMapping("/rateCoupon/coupon/list")
    Map<Long, RateCoupon> findRateCouponByCouponIdList(@RequestParam("couponIdList") List<Long> rateCouponIds);

    // 고정할인쿠폰
    @GetMapping("/fixCoupon/coupon/list")
    List<Long> findFixCouponByCouponIdList(@RequestParam("couponIdList") List<Long> fixCouponIds);
}
