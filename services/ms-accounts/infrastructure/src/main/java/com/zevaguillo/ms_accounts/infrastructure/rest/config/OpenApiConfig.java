package com.zevaguillo.ms_accounts.infrastructure.rest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msAccountsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-accounts API")
                        .description("Microservicio de gestión de cuentas bancarias")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Zevaguillo")
                                .email("dev@zevaguillo.com")));
    }
}