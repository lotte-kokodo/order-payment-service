package shop.kokodo.orderservice.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import shop.kokodo.orderservice.DocumentConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import shop.kokodo.orderservice.dto.request.CartOrderDto;
import shop.kokodo.orderservice.dto.request.SingleProductOrderDto;
import shop.kokodo.orderservice.message.DtoValidationMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

/**
 * packageName    : order-payment-service
 * fileName       : OrderControllerTest
 * author         : tngh1
 * date           : 2022-11-05
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022-11-05        tngh1              최초 생성
 */
class OrderControllerTest extends DocumentConfiguration {
    @PersistenceContext
    private EntityManager em;

    @AfterEach
    public void tearDown() {
        em.unwrap(Session.class)
                .doWork(this::cleanUpTable);
    }

    private void cleanUpTable(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("SET REFERENTIAL_INTEGRITY FALSE");

        statement.executeUpdate("TRUNCATE TABLE \"ORDERS\"");
        statement.executeUpdate("TRUNCATE TABLE \"ORDER_PRODUCT\"");

        statement.executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @Test
    @DisplayName("주문 내역 조회")
    public void orderList() throws Exception{
        //given
        Long memberId = 1L;
        //when
        final ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .filter(document("get-order-list"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .request()
                .header("memberId", 1)
                .param("page", 1)
                .get("/orders/")
                .then().log().all().extract();
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("주문 상세 내역 조회")
    public void orderDetailList() throws Exception {
        //given
        Long memberId = 1L;
        Long orderId = 1L;
        //when
        final ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .filter(document("get-order-detail-list"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header("memberId", 1)
                .get("/orders/{orderId}", orderId)
                .then().log().all().extract();
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("단일 상품 주문")
    public void orderSingleProduct() {
        // given
        Long memberId = 1L;
        Long productId = 50L;
        Long sellerId = 1L;
        Integer qty = 3;
        Long rateCouponId = 3L;
        Long fixCouponId = 2L;

        SingleProductOrderDto reqBody = new SingleProductOrderDto(memberId, productId,
            sellerId, qty, rateCouponId, fixCouponId);

        // when
        final ExtractableResponse<Response> resp = RestAssured
            .given(spec).log().all()
            .filter(document("order-single-product"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .header("memberId", 1)
            .request().body(reqBody)
            .get("/orders/singleProduct")
            .then().log().all().extract();

        // then
        assertThat(resp.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("장바구니 상품 주문")
    public void orderCartProduct() {

        // given
        Long memberId = 1L;
        List<Long> cartIds = List.of(1L, 2L, 3L);
        List<Long> rateCouponIds = List.of(1L, 2L, 3L);
        List<Long> fixCouponIds = List.of(1L, 2L, 3L);

        CartOrderDto reqBody = new CartOrderDto(memberId, cartIds, rateCouponIds,fixCouponIds);

        // when
        final ExtractableResponse<Response> resp = RestAssured
            .given(spec).log().all()
            .filter(document("order-cart"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .header("memberId", 1)
            .request().body(reqBody)
            .get("/orders/singleProduct")
            .then().log().all().extract();

        // then
        assertThat(resp.statusCode()).isEqualTo(HttpStatus.OK.value());

    }


//    @DisplayName("정산 예정날짜 조회")
//    @Test
//    public void calculateExpectDay() throws Exception{
//        //given
//        //when
//        final ExtractableResponse<Response> response = RestAssured.
//                given(spec).log().all()
//                .filter(document("calculate-expectDay"))
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when().get("/calculate/expectDay")
//                .then().log().all().extract();
//        //then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//    }

    @Test
    @DisplayName("상품 아이디로 주문 내역 조회")
    public void findByProductId() {
        //given
        List<Long> productIdList = new ArrayList<>();
        productIdList.add(1L);
        productIdList.add(2L);
        String productIdString = "";
        for(Long productId : productIdList) {
            productIdString += productId + ",";
        }
        productIdString = productIdString.substring(0, productIdString.length() - 1);
        //when
        final ExtractableResponse<Response> response = RestAssured
                .given(spec).log().all()
                .filter(document("get-find-by-product-id"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .request().param("productIdList", productIdString)
                .get("/orders/feign/product")
                .then().log().all().extract();
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}