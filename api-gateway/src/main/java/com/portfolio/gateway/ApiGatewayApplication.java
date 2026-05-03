package com.portfolio.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        Dotenv.configure()
                .directory("../")
                .ignoreIfMissing()
                .load()
                .entries()
                .forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}