package shop.kokodo.orderservice.messagequeue.request;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import shop.kokodo.orderservice.messagequeue.KafkaMessageType;

public class KafkaRequest {



    /*
    * 상품
    * */

    @AllArgsConstructor
    @Getter
    @ToString
    public static class ProductUpdateStock {
        private Long productId;
        private Integer qty;
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class ProductUpdateStockMap {
        private Map<Long, Integer> map;
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class KafkaMessage<T> {
        private KafkaMessageType type;
        private T data;
    }


    /*
    * 쿠폰
    * */
    @AllArgsConstructor
    @Getter
    @ToString
    public static class CouponUpdateStatus {
        private Long couponId;
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class CouponUpdateStatusList {
        private List<Long> couponIds;
    }

}
