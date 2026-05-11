package com.portfolio.reportservice.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {

        return requestTemplate -> {

            RequestAttributes requestAttributes =
                    RequestContextHolder.getRequestAttributes();

            if (requestAttributes instanceof ServletRequestAttributes attributes) {

                HttpServletRequest request =
                        attributes.getRequest();

                String userId =
                        request.getHeader("X-User-Id");

                String authorization =
                        request.getHeader("Authorization");

                if (userId != null) {
                    requestTemplate.header(
                            "X-User-Id",
                            userId
                    );
                }

            }
        };
    }
}