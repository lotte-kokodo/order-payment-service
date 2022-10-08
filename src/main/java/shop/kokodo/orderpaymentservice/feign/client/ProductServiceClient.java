package shop.kokodo.orderpaymentservice.feign.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;

@FeignClient(name = "product-service", path = "/products") // application name
public interface ProductServiceClient {

    @GetMapping("/unitPrice/{productId}")
    FeignResponse.ProductPrice getProduct(@PathVariable Long productId);

    @GetMapping("/unitPrice")
    List<FeignResponse.ProductPrice> getProducts(@ModelAttribute List<Long> productIds);

    @GetMapping("/cart")
    Map<Long, FeignResponse.ProductOfCart> getCartProducts(@ModelAttribute List<Long> productIds);
}
