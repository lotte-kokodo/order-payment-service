package shop.kokodo.orderservice.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.kokodo.orderservice.message.DtoValidationMessage;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberIdDto {

    @NotBlank(message = DtoValidationMessage.MEMBER_ID_NULL)
    private Long memberId;

}
