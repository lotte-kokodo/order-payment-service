package shop.kokodo.orderpaymentservice.entity.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnumMapper {
    private Map<String, List<EnumValue>> factory = new HashMap<>();

    private List<EnumValue> toEnumValues(Class<? extends EnumType> e) {
        return Arrays.stream(e.getEnumConstants())
                .map(EnumValue::new)
                .collect(Collectors.toList());
    }

    public void put(String key, Class<? extends EnumType> e) {
        factory.put(key, toEnumValues(e));
    }

    public Map<String, List<EnumValue>> getAll() {
        return factory;
    }

    public Map<String, List<EnumValue>> get(String keys) {

        // Function identity 는 인자로 받아온 값의 타입을 반환한다.
        return Arrays.stream(keys.split(","))
                .collect(Collectors.toMap(Function.identity(), key -> factory.get(key)));
    }
}
