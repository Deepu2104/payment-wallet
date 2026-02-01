package com.paywallet.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PayWalletApplication {

    public static void main(String[] args) {
        // Fix Render's DB URL (postgres:// or postgresql:// -> jdbc:postgresql://)
        // AND strip credentials because JDBC driver might reject user:pass@host format
        String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
        System.out.println("DEBUG: Original DB URL: " + dbUrl);

        if (dbUrl != null) {
            try {
                // Remove "jdbc:" prefix if present to parse as standard URI
                String cleanUriString = dbUrl;
                if (dbUrl.startsWith("jdbc:")) {
                    cleanUriString = dbUrl.substring(5);
                }

                java.net.URI uri = new java.net.URI(cleanUriString);
                String host = uri.getHost();
                int port = uri.getPort();
                String path = uri.getPath();

                // Construct clean JDBC URL: jdbc:postgresql://host:port/db
                String cleanUrl = "jdbc:postgresql://" + host + (port == -1 ? "" : ":" + port) + path;

                System.out.println("DEBUG: Fixed Clean DB URL: " + cleanUrl);
                System.setProperty("SPRING_DATASOURCE_URL", cleanUrl);
                System.setProperty("spring.datasource.url", cleanUrl);
            } catch (Exception e) {
                System.out.println("DEBUG: Failed to parse DB URL: " + e.getMessage());
                // Fallback: minimal replacement if parsing fails
                if (dbUrl.startsWith("postgres://")) {
                    String newUrl = dbUrl.replace("postgres://", "jdbc:postgresql://");
                    System.setProperty("SPRING_DATASOURCE_URL", newUrl);
                    System.setProperty("spring.datasource.url", newUrl);
                } else if (dbUrl.startsWith("postgresql://")) {
                    String newUrl = dbUrl.replace("postgresql://", "jdbc:postgresql://");
                    System.setProperty("SPRING_DATASOURCE_URL", newUrl);
                    System.setProperty("spring.datasource.url", newUrl);
                }
            }
        }

        SpringApplication.run(PayWalletApplication.class, args);
    }

}
