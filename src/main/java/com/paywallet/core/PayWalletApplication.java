package com.paywallet.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PayWalletApplication {

    public static void main(String[] args) {
        // Fix Render's DB URL (postgres:// or postgresql:// -> jdbc:postgresql://)
        String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
        System.out.println("DEBUG: Original DB URL: " + dbUrl);

        if (dbUrl != null) {
            String newUrl = null;
            if (dbUrl.startsWith("postgres://")) {
                newUrl = dbUrl.replace("postgres://", "jdbc:postgresql://");
            } else if (dbUrl.startsWith("postgresql://")) {
                newUrl = dbUrl.replace("postgresql://", "jdbc:postgresql://");
            }

            if (newUrl != null) {
                System.out.println("DEBUG: Fixed DB URL: " + newUrl);
                System.setProperty("SPRING_DATASOURCE_URL", newUrl);
                System.setProperty("spring.datasource.url", newUrl);
            }
        }

        SpringApplication.run(PayWalletApplication.class, args);
    }

}
