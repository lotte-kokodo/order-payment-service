package shop.kokodo.orderpaymentservice.service;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import shop.kokodo.orderpaymentservice.dto.response.dto.ProductResponse;

// TODO: Member API 및 Application Name 확인
@FeignClient(name = "product-service") // product-service 의 application name
public interface ProductServiceClient {

    @GetMapping("/products/{productId}")
    ProductResponse getProduct(@PathVariable Long productId);

    @GetMapping("/products")
    List<ProductResponse> getProducts(@ModelAttribute List<Long> productIds);
}
