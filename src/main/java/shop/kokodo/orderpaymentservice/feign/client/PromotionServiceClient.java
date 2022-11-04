package shop.kokodo.orderpaymentservice.feign.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.RateCoupon;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.RateDiscountPolicy;

@FeignClient(name = "promotion-service")
public interface PromotionServiceClient {

    // 비율할인정책
    // [key] 상품아이디 [value] 상품에 적용된 비율할인정책
    @GetMapping("/rate-discount/list")
    Map<Long, RateDiscountPolicy> getRateDiscountPolicy(@RequestParam("productIdList") List<Long> productIds);

    // 고정할인정책
    // [key] 판매자 아이디 [value] 판매자의 고정할인정책 적용 유무
    @GetMapping("/feign/fix-discount/status")
    Map<Long, Boolean> getFixDiscountPolicyStatusForFeign(@RequestParam List<Long> productIdList, @RequestParam List<Long> sellerIdList);

    // 비율할인쿠폰
    // [key] 상품아이디 [value] 상품에 적용된 비율할인쿠폰
    @GetMapping("/rateCoupon/coupon/list")
    Map<Long, RateCoupon> findRateCouponByCouponIdList(@RequestParam("couponIdList") List<Long> rateCouponIds);

    // 고정할인쿠폰
    // 판매자 아이디 리스트 - 고정할인쿠폰을 적용한 판매자 리스트
    @GetMapping("/fixCoupon/coupon/list")
    List<Long> findFixCouponByCouponIdList(@RequestParam("couponIdList") List<Long> fixCouponIds);
}
