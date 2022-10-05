package shop.kokodo.orderpaymentservice.service.interfaces.client;

import org.apache.kafka.common.message.LeaveGroupResponseData.MemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;

// TODO: Member API 및 Application Name 확인
@FeignClient(name = "member-service") // member-service 의 application name
public interface MemberServiceClient {

    @GetMapping("/member-service/delivery-info/{memberId}")
    FeignResponse.MemberDeliveryInfo getMember(@PathVariable Long memberId);

}
