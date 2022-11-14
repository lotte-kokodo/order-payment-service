package shop.kokodo.orderservice.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;

public class CartControllerTest {
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

        statement.executeUpdate("TRUNCATE TABLE \"CART\"");

        statement.executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");
    }


}
