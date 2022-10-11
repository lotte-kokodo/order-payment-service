package shop.kokodo.orderpaymentservice.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;

@FeignClient(name = "member-service", path = "/members/feign") // application name
public interface MemberServiceClient {

    @GetMapping("/address")
    FeignResponse.MemberAddress getMemberAddress(@RequestHeader Long memberId);

    @GetMapping("/orderInfo")
    FeignResponse.MemberOfOrderSheet getMemberOrderInfo(@RequestHeader Long memberId);
}
