package shop.kokodo.orderpaymentservice.entity.enums;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.kokodo.orderpaymentservice.entity.enums.status.OrderStatus;
import shop.kokodo.orderpaymentservice.entity.enums.status.PaymentMethod;
import shop.kokodo.orderpaymentservice.entity.enums.status.PaymentStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("[EnumMapperConfig]")
class EnumMapperConfigTest {

    static EnumMapper mapper;

    @BeforeAll
    static void setUp() {
        mapper = new EnumMapper();
        mapper.put("OrderStatus", OrderStatus.class);
        mapper.put("PaymentStatus", PaymentStatus.class);
        mapper.put("PaymentMethod", PaymentMethod.class);
    }

    @Nested
    @DisplayName("성공 로직 테스트 케이스")
    class SuccessCase {

        @Test
        @DisplayName("EnumMapper 에 유효한 Key 를 입력했을 때 열거형 리스트 출력")
        void Input_ValidKey_Output_EnumList() {
            Map<String, List<EnumValue>> orderStatus = mapper.get("OrderStatus");
            List<EnumValue> orderStatusList = orderStatus.get("OrderStatus");

            Assertions.assertEquals(orderStatusList.get(0).getValue(), OrderStatus.ORDER_SUCCESS.getValue());
            Assertions.assertEquals(orderStatusList.get(1).getValue(), OrderStatus.PURCHASE_CONFIRM.getValue());
            Assertions.assertEquals(orderStatusList.get(2).getValue(), OrderStatus.REFUND_PROCESS.getValue());
        }
    }

    @Nested
    @DisplayName("실패 로직 테스트 케이스")
    class FailureCase {

        @Test
        @DisplayName("EnumMapper 에 유효하지 않은 Key 를 입력했을 때 NullPointException 출력")
        void Input_InvalidKey_Output_NullPointerException() {

            Assertions.assertThrows(NullPointerException.class, () -> {
                mapper.get("NotDefinedEnum");
            });

        }

    }
}