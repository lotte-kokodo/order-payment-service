package shop.kokodo.orderpaymentservice.dto.response.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class IdAndMessageDto {

    @AllArgsConstructor
    @Getter
    public static class CreateSuccess {

        private Long id;
        private String msg; // 주문 등록 성공/실패 여부

    }

}
