package shop.kokodo.orderservice.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import shop.kokodo.orderservice.feign.response.OrderMemberDto;

@FeignClient(name = "member-service") // application name
public interface MemberServiceClient {

    @GetMapping("/members/feign/order")
    OrderMemberDto getOrderMember(@RequestHeader Long memberId);

    @GetMapping("/member/check/info")
    Boolean checkMemberInfoApplied(@RequestHeader Long memberId);
}
