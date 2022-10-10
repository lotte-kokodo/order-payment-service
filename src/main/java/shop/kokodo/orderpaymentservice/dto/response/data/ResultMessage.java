package shop.kokodo.orderpaymentservice.dto.response.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResultMessage {
    private Long id;
    private String msg; // 주문 등록 성공/실패 여부
}
