package shop.kokodo.orderpaymentservice.feign.response;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class FeignResponse {

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductPrice {
        Long id;
        Integer price;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDeliveryInfo {

        private String address;
        private String name;

    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOfOrder {
        private Long id;
        private String thumbnail;
        private String name;
        private Long sellerId;
        private Integer price;
    }


    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberOfOrderSheet {
        private String name;
        private String email;
        private String phoneNumber;
        private String address;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductStock {
        private Long id;
        private Integer stock;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RateDiscountPolicy {
        private Long id;
        private Integer rate; // 할인비율
        private Long productId;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FixDiscountPolicy {
        private Long id;
        private Long price; // 할인가격
        private Long productId;
    }
}
