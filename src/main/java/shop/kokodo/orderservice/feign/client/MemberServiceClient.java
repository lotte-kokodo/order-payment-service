package shop.kokodo.orderservice.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import shop.kokodo.orderservice.feign.response.FeignResponse.MemberDeliveryInfo;

@FeignClient(name = "member-service", path = "/members/feign") // application name
public interface MemberServiceClient {

    @GetMapping("/deliveryInfo")
    MemberDeliveryInfo getMemberDeliveryInfo(@RequestHeader Long memberId);

}
