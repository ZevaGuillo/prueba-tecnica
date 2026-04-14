package com.zevaguillo.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.zevaguillo.infrastructure",
    "com.zevaguillo.application"
})
@EnableJpaRepositories("com.zevaguillo.infrastructure.persistence.entity")
public class MsClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsClientApplication.class, args);
    }
}