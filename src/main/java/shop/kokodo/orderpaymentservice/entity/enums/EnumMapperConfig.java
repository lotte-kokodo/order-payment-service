package shop.kokodo.orderpaymentservice.entity.enums;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.kokodo.orderpaymentservice.entity.enums.status.OrderStatus;
import shop.kokodo.orderpaymentservice.entity.enums.status.PaymentMethod;
import shop.kokodo.orderpaymentservice.entity.enums.status.PaymentStatus;

@Configuration
public class EnumMapperConfig {

    @Bean
    public EnumMapper enumMapper() {
        EnumMapper enumMapper = new EnumMapper();
        enumMapper.put("OrderStatus", OrderStatus.class);
        enumMapper.put("PaymentStatus", PaymentStatus.class);
        enumMapper.put("PaymentMethod", PaymentMethod.class);
        return enumMapper;
    }
}