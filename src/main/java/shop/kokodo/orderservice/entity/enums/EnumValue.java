package shop.kokodo.orderservice.entity.enums;

import lombok.Getter;

@Getter
public class EnumValue {

    private String key;
    private String value;

    public EnumValue(EnumType enumType) {
        this.key = enumType.getKey();
        this.value = enumType.getValue();
    }
}