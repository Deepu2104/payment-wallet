package com.paywallet.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PayWalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayWalletApplication.class, args);
    }

}
