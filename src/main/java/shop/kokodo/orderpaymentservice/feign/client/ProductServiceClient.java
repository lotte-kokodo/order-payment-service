package shop.kokodo.orderpaymentservice.feign.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse;
import shop.kokodo.orderpaymentservice.feign.response.FeignResponse.ProductOfOrder;

@FeignClient(name = "product-service", path = "/products/feign") // application name
public interface ProductServiceClient {

    @GetMapping("/unitPrice/{productId}")
    FeignResponse.ProductPrice getProduct(@PathVariable Long productId);

    @GetMapping("/unitPrice")
    List<FeignResponse.ProductPrice> getProducts(@ModelAttribute List<Long> productIds);

    @GetMapping("/order")
    Map<Long, ProductOfOrder> getOrderProducts(@RequestParam List<Long> productIds);

    @GetMapping("/stock/{productId}")
    FeignResponse.ProductStock getProductStock(@PathVariable Long productId);

}
