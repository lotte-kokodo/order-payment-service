package shop.kokodo.orderpaymentservice.dto.response;

import lombok.AllArgsConstructor;

public class OrderResponse {

    @AllArgsConstructor
    public static class CreateOrder {

        private Long id;
        private String msg; // 주문 등록 성공/실패 여부

    }


}
