package shop.kokodo.orderpaymentservice.feign.response;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

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
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RateDiscountPolicy {
        private Long rateDiscountPolicyId;
        private Integer rate; // 할인비율
        private Long productId;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixDiscountPolicy {
        private Long fixDiscountPolicyId;
        private Long price; // 할인가격
        private Long productId;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RateCoupon {
        private Long id;
        private String name;
        private LocalDateTime regdate;
        private Integer rate;
        private Integer minPrice;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Long productId;
        private Long sellerId;

    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixCoupon {

        private Long id;
        private String name;
        private LocalDateTime regdate;
        private Integer price;
        private Integer minPrice;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Long productId;
        private Long sellerId;
        private boolean freeDelivery;
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product {
        private Long id;
        private String name;
        private String displayName;
        private String thumbnail;

    }

    public static Map<Long, Product> ProductMap;
}
