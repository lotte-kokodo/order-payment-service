package shop.kokodo.orderservice.aop;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import shop.kokodo.orderservice.exception.api.ApiRequestException;
import shop.kokodo.orderservice.feign.client.MemberServiceClient;
import shop.kokodo.orderservice.message.ExceptionMessage;

@Aspect
@Slf4j
@Component
public class MemberInfoCheckAspect {

    private final MemberServiceClient memberServiceClient;

    public MemberInfoCheckAspect(
        MemberServiceClient memberServiceClient) {
        this.memberServiceClient = memberServiceClient;
    }

    @Before(value = "@annotation(memberInfoCheck)")
    public void memberInfoCheck(MemberInfoCheck memberInfoCheck) {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request =requestAttributes.getRequest();

        Long memberId = Long.parseLong(request.getHeader("memberId"));
        Boolean isMemberInfoRegistered = memberServiceClient.checkMemberInfoApplied(memberId);
        if (notRegistered(isMemberInfoRegistered)) {
            log.debug("회원 배송정보 미등록");
            throw new ApiRequestException(ExceptionMessage.NOT_REGISTERED_MEMBER_INFO);
        }
    }

    private Boolean notRegistered(Boolean bool) {
        return !bool;
    }

}
