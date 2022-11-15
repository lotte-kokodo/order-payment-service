package shop.kokodo.orderservice.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * packageName : shop.kokodo.orderservice.config
 * fileName : QueryDslConfig
 * author : BTC-N24
 * date : 2022-11-15
 * description :
 * ======================================================
 * DATE                AUTHOR                NOTE
 * ======================================================
 * 2022-11-15           BTC-N24              최초 생성
 */
@Configuration
public class QueryDslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
