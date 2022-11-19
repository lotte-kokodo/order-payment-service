package shop.kokodo.orderservice.feign.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import shop.kokodo.orderservice.feign.request.OrderCountRequestDto;
import shop.kokodo.orderservice.feign.response.ProductIdResponseDto;
import shop.kokodo.orderservice.feign.response.OrderProductDto;
import shop.kokodo.orderservice.feign.response.CartProductDto;
import shop.kokodo.orderservice.feign.response.ProductStockDto;
import shop.kokodo.orderservice.feign.response.ProductThumbnailDto;

@FeignClient(name = "product-service", path = "/products/feign") // application name
public interface ProductServiceClient {

    /**
     * 단일상품 주문을 위한 상품 조회 API
     */
    @GetMapping("/singleOrderProduct")
    OrderProductDto getSingleOrderProduct(@RequestParam Long productId);

    /**
     * 장바구니상품 주문을 위한 상품조회 API
     */
    @GetMapping("/cartOrderProduct")
    Map<Long, OrderProductDto> getCartOrderProduct(@RequestParam List<Long> productIds);

    /**
     * 장바구니 목록 상품조회 API
     */
    @GetMapping("/cart")
    Map<Long, CartProductDto> getOrderProducts(@RequestParam List<Long> productIds);

    /**
     * 장바구니 상품 수량 업데이트 상품조회 API
     */
    @GetMapping("/stock/{productId}")
    ProductStockDto getProductStock(@PathVariable Long productId);

    /**
     * 주문서 상품조회 API
     */
    @GetMapping("/list")
    Map<Long, ProductThumbnailDto> getProductList(@RequestParam List<Long> productIdList);

    /**
     * 판매자의 당일주문상품 개수 조회
     */
    @PostMapping("/seller/orderCount")
    ProductIdResponseDto getOrderCount(@RequestBody OrderCountRequestDto orderCountRequestDto);

    /**
     * 판매자의 당월상품 개수 조회를 위한 상품아이디리스트 조회
     */
    @GetMapping("/seller/{sellerId}/productId")
    List<Long> getSellerProductIds(@PathVariable Long sellerId);

    @GetMapping("/list/map")
    Map<Long, ProductThumbnailDto> getProductListMap(@RequestParam List<Long> productIdList);
}
