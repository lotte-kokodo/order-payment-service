package shop.kokodo.orderservice.service.utils;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import shop.kokodo.orderservice.entity.OrderProduct;
import shop.kokodo.orderservice.feign.response.RateCouponDto;
import shop.kokodo.orderservice.feign.response.RateDiscountPolicyDto;

@Component
public class ProductPriceCalculator {

    private static final Integer DELIVERY_PRICE = 3000;

    /**
     * @param orderProducts 주문상품 리스트
     * @param productSellerMap 상품아이디-팬매자아이디 맵
     * @param rateDiscountProductMap 상품아이디-할인비율 맵
     * @param fixDiscountPolicySellerMap 판매자아이디-고정할인정책 맵
     * @param rateCouponMap 상품아이디-비율할인쿠폰 맵
     * @param fixCouponSellerIds 고정할인쿠폰을 발급한 판매자 리스트
     * @return
     */
    public Integer calcTotalPrice(List<OrderProduct> orderProducts,
        Map<Long, Long> productSellerMap,
        Map<Long, RateDiscountPolicyDto> rateDiscountProductMap,
        Map<Long, Boolean> fixDiscountPolicySellerMap,
        Map<Long, RateCouponDto> rateCouponMap,
        List<Long> fixCouponSellerIds) {

        Integer totalPrice = orderProducts.stream()
            .map(orderProduct -> orderProduct.getUnitPrice()*orderProduct.getQty())
            .mapToInt(Integer::intValue).sum();

        Integer discountPrice = orderProducts.stream()
            .map(orderProduct -> {
                Long productId = orderProduct.getProductId();
                RateDiscountPolicyDto rateDiscountPolicy = rateDiscountProductMap.get(productId);
                Integer unitPrice = orderProduct.getUnitPrice();
                Integer qty = orderProduct.getQty();

                return calcDiscountPrice(unitPrice, qty, rateDiscountPolicy, rateCouponMap.get(productId));
            })
            .mapToInt(Integer::intValue)
            .sum();

        Integer deliveryPrice = orderProducts.stream()
            .map(orderProduct -> {
                Long productId = orderProduct.getProductId();
                Long sellerId = productSellerMap.get(productId);
                return calcDeliveryPrice(sellerId, fixDiscountPolicySellerMap, fixCouponSellerIds);
            })
            .mapToInt(Integer::intValue)
            .sum();

        return totalPrice - discountPrice + deliveryPrice;
    }

    /**
     * @param unitPrice 상품 가격
     * @param qty 상품 수량
     * @param rateDiscountPolicy 비율할인정책 - 할인비율(정수값)
     * @param rateCoupon 비율할인쿠폰 - 할인비율(정수값)
     * @return 상품에 대한 총 할인가격
     */
    protected Integer calcDiscountPrice(Integer unitPrice, Integer qty,
        RateDiscountPolicyDto rateDiscountPolicy, RateCouponDto rateCoupon) {

        Integer productPrice = unitPrice*qty;
        Integer discPrice = 0;

        if (isExistRateDiscountPolicy(rateDiscountPolicy)) {
            discPrice += (int) (productPrice*(rateDiscountPolicy.getRate()*0.01));
        }

        if (isExistRateCoupon(rateCoupon)) {
            discPrice += (int) ( productPrice*(rateCoupon.getRate()*0.01));
        }

        return discPrice;
    }

    /**
     * @param sellerId 판매자 아이디
     * @param fixDiscountPolicySellerMap 고정할인정책 맵 - 판매자의 고정할인정책 적용 유무(true/false)
     * @param fixCouponSellerIds 고정할인쿠폰 적용한 판매자 리스트
     * @return
     */
    protected Integer calcDeliveryPrice(Long sellerId, Map<Long, Boolean> fixDiscountPolicySellerMap, List<Long> fixCouponSellerIds) {
        Integer deliveryPrice = 0;

        // 배달관련정책쿠폰(고정할인비율 및 쿠폰)이 없는 경우 배달료 추가
        if (notFixDiscountPolicyApplied(fixDiscountPolicySellerMap.get(sellerId))) {
            if (notContainFixCoupon(sellerId, fixCouponSellerIds)) {
                deliveryPrice += DELIVERY_PRICE;
            }
        }
        return deliveryPrice;
    }

    protected boolean isExistRateDiscountPolicy(RateDiscountPolicyDto rateDiscountPolicy) {
        return rateDiscountPolicy != null;
    }

    protected boolean isExistRateCoupon(RateCouponDto rateCoupon) {
        return rateCoupon != null;
    }

    protected boolean notFixDiscountPolicyApplied(Boolean discountPolicyBool) {
        return !discountPolicyBool;
    }

    protected boolean notContainFixCoupon(Long sellerId, List<Long> fixCouponSellerIds) {
        return !fixCouponSellerIds.contains(sellerId);
    }
}
