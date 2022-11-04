package shop.kokodo.orderpaymentservice.service.utils;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import shop.kokodo.orderpaymentservice.entity.OrderProduct;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.RateCoupon;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.RateDiscountPolicy;

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
        Map<Long, RateDiscountPolicy> rateDiscountProductMap,
        Map<Long, Boolean> fixDiscountPolicySellerMap,
        Map<Long, RateCoupon> rateCouponMap,
        List<Long> fixCouponSellerIds) {

        Integer totalPrice = orderProducts.stream()
            .map(orderProduct -> orderProduct.getUnitPrice()*orderProduct.getQty())
            .mapToInt(Integer::intValue).sum();

        Integer discountPrice = orderProducts.stream()
            .map(orderProduct -> {
                Long productId = orderProduct.getProductId();
                RateDiscountPolicy rateDiscountPolicy = rateDiscountProductMap.get(productId);
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
        RateDiscountPolicy rateDiscountPolicy, RateCoupon rateCoupon) {

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
        // OrderProduct 리스트를 순차적으로 반복하며 수행하는 로직이므로 여러 상품들이 동일한 판매자 아이디를 가지는 경우 발생
        // 정책및쿠폰이 한 번 적용되면 맵, 리스트에서 제거
        if (notExistFixDiscountPolicy(fixDiscountPolicySellerMap.get(sellerId))) {
            deliveryPrice += DELIVERY_PRICE;
            fixDiscountPolicySellerMap.remove(sellerId);
        }
        else if (notContainFixCoupon(sellerId, fixCouponSellerIds)) {
            deliveryPrice += DELIVERY_PRICE;
            fixCouponSellerIds.remove(sellerId);
        }
        return deliveryPrice;
    }

    protected boolean isExistRateDiscountPolicy(RateDiscountPolicy rateDiscountPolicy) {
        return rateDiscountPolicy != null;
    }

    protected boolean isExistRateCoupon(RateCoupon rateCoupon) {
        return rateCoupon != null;
    }

    protected boolean notExistFixDiscountPolicy(Boolean discountPolicyBool) {
        return discountPolicyBool == null || discountPolicyBool;
    }

    protected boolean notContainFixCoupon(Long sellerId, List<Long> fixCouponSellerIds) {
        return !fixCouponSellerIds.contains(sellerId);
    }
}
