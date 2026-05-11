package com.portfolio.user;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        Dotenv.configure()
                .directory("./")        // root .env — same level as pom.xml
                .ignoreIfMissing()
                .load()
                .entries()
                .forEach(e -> System.setProperty(e.getKey(), e.getValue()));

        SpringApplication.run(UserServiceApplication.class, args);
    }
}