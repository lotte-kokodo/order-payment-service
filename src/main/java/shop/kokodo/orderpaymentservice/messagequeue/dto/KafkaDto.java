package shop.kokodo.orderpaymentservice.messagequeue.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.ToString;
import shop.kokodo.orderpaymentservice.messagequeue.KafkaMessageType;

public class KafkaDto {



    /*
    * 상품
    * */
    @AllArgsConstructor
    @ToString
    public static class ProductUpdateStock {
        private Long productId;
        private Integer qty;
    }

    @AllArgsConstructor
    @ToString
    public static class ProductUpdateStockList {
        private Map<Long, Integer> productIdQtyMap;
    }

    @AllArgsConstructor
    public static class ProductUpdateStockTypeMessage<T> {
        private KafkaMessageType type;
        private T dto;
    }


    /*
    * 쿠폰
    * */
    @AllArgsConstructor
    @ToString
    public static class CouponUpdateStatus {
        private Long couponId;
    }

    @AllArgsConstructor
    @ToString
    public static class CouponUpdateStatusList {
        private List<Long> couponIds;
    }

}
