package shop.kokodo.orderservice.config.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter @Component
@ConfigurationProperties("spring.datasource")
public class DbProperty {

    private String url;
    private Slave slaveList;

    private String driverClassName;
    private String username;
    private String password;

    @Getter @Setter
    public static class Slave {
        private String slave1;
        private String slave2;
    }
}