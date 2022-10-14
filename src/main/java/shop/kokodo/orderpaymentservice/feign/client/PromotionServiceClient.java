package shop.kokodo.orderpaymentservice.feign.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.FixDiscountPolicy;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.RateDiscountPolicy;

@FeignClient(name = "promotion-service") // application name
public interface PromotionServiceClient {

    // 비율할인정책
    @GetMapping("/rate-discount/list")
    Map<Long, RateDiscountPolicy> getRateDiscountPolicy(@RequestParam("productIdList") List<Long> productIds);

    // 고정할인정책
    @GetMapping("/fix-discount/list")
    Map<Long, FixDiscountPolicy> getFixDiscountPolicy(@RequestParam("productIdList") List<Long> productIds);

}
