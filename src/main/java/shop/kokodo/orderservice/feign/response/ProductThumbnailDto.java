package shop.kokodo.orderservice.feign.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductThumbnailDto {
    private Long id;
    private String name;
    private String displayName;
    private String thumbnail;
}
