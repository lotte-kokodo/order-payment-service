package shop.kokodo.orderpaymentservice.feign.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ProductDto {

    private Long id;
    private String thumbnail;
    private String name;
    private Long sellerId;
    private Integer price;

    public static ProductDto create(Long id, String thumbnail, String name, Integer price, Long sellerId) {
        return new ProductDto(id, thumbnail, name, price, sellerId);
    }

    @Builder
    public ProductDto(Long id, String thumbnail, String name, Integer price, Long sellerId) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.name = name;
        this.price = price;
        this.sellerId = sellerId;
    }
}
