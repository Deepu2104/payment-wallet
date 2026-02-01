package com.paywallet.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PayWalletApplication {

    public static void main(String[] args) {
        // Fix Render's DB URL (postgres:// -> jdbc:postgresql://)
        String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (dbUrl != null && dbUrl.startsWith("postgres://")) {
            System.setProperty("SPRING_DATASOURCE_URL", dbUrl.replace("postgres://", "jdbc:postgresql://"));
        }

        SpringApplication.run(PayWalletApplication.class, args);
    }

}
