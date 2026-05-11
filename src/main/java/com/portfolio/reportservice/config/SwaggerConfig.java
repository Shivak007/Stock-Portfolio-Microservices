package com.portfolio.reportservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title(
                                        "Stock Portfolio Report Service API"
                                )
                                .version("1.0")
                                .description(
                                        "Microservice for generating PDF and Excel portfolio reports"
                                )
                );
    }
}