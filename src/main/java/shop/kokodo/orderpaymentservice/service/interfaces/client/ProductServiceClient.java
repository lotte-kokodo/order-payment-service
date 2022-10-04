package shop.kokodo.orderpaymentservice.service.interfaces.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import shop.kokodo.orderpaymentservice.dto.feign.response.FeignResponse;

// TODO: Product API 및 Application Name 확인
@FeignClient(name = "product-service") // product-service 의 application name
public interface ProductServiceClient {

    @GetMapping("/product-service/unit-price/{productId}")
    FeignResponse.ProductPrice getProduct(@PathVariable Long productId);

    @GetMapping("/product-service/unit-price")
    List<FeignResponse.ProductPrice> getProducts(@ModelAttribute List<Long> productIds);
}
