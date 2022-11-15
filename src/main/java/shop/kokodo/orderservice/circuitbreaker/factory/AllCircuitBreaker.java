package shop.kokodo.orderservice.circuitbreaker.factory;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;

/**
 * packageName    : shop.kokodo.orderservice.circuitbreaker.factory
 * fileName       : AllCircuitBreaker
 * author         : SSOsh
 * date           : 2022/11/05
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/10/26        SSOsh             최초 생성
 */
@Component
public class AllCircuitBreaker {

    private static CircuitBreakerFactory circuitBreakerFactory;

    public AllCircuitBreaker(CircuitBreakerFactory circuitBreakerFactory) {
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public static CircuitBreaker createSellerCircuitBreaker() {
        return circuitBreakerFactory.create("sellerCircuitBreaker");
    }
}
