package shop.kokodo.orderpaymentservice.dto.response.dto;

import lombok.AllArgsConstructor;

public class IdAndMessageDto {

    @AllArgsConstructor
    public static class CreateSuccess {

        private Long id;
        private String msg; // 주문 등록 성공/실패 여부

    }

}
