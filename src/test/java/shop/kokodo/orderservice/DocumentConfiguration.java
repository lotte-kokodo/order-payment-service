package shop.kokodo.orderservice;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

/**
 * packageName    : order-payment-service
 * fileName       : DocumentConfiguration
 * author         : tngh1
 * date           : 2022-11-05
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022-11-05        tngh1              최초 생성
 */
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public abstract class DocumentConfiguration extends AcceptanceTest{
    public RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withResponseDefaults(prettyPrint())
                        .withRequestDefaults(prettyPrint())
                )
                .build();
    }
}
