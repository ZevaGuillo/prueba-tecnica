package com.zevaguillo.ms_accounts.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zevaguillo.ms_accounts"})
@EnableJpaRepositories(basePackages = {"com.zevaguillo.ms_accounts.infrastructure.persistence.entity"})
public class MsAccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsAccountsApplication.class, args);
    }
}