package com.banco.msclients.infrastructure.rest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msClientsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-clients API")
                        .description("Microservicio de gestión de clientes")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Banco")
                                .email("dev@banco.com")));
    }
}