package shop.kokodo.orderservice.feign.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class OrderMemberDto {
    private String address;
    private String name;
}
