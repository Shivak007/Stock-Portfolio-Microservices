package com.portfolio.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {
    public static void main(String[] args) {
        Dotenv.configure()
                .directory("./")       
                .ignoreIfMissing()
                .load()
                .entries()
                .forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}