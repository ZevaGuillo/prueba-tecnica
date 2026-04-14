package com.zevaguillo.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class MsReportesApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsReportesApplication.class, args);
    }
}