package shop.kokodo.orderservice.dto.response.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailInformationDto {

    private Long id;

    private String name;

    private String thumbnail;

    private int price;

    private int qty;

}
