package shop.kokodo.orderservice.feign.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import shop.kokodo.orderservice.feign.response.FeignResponse;
import shop.kokodo.orderservice.feign.response.ProductDto;

@FeignClient(name = "product-service", path = "/products/feign") // application name
public interface ProductServiceClient {

    @GetMapping("/unitPrice/{productId}")
    FeignResponse.ProductPrice getProductPrice(@PathVariable Long productId);

    @GetMapping("/unitPrice")
    Map<Long, Integer> getProductsPrice(@RequestParam List<Long> productIds);

    @GetMapping("/order")
    Map<Long, ProductDto> getOrderProducts(@RequestParam List<Long> productIds);

    @GetMapping("/stock/{productId}")
    FeignResponse.ProductStock getProductStock(@PathVariable Long productId);

    @GetMapping("/list/map")
    public Map<Long, FeignResponse.Product> getProductListMap(@RequestParam List<Long> productIdList);
}
