package com.portfolio.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/oauth2/"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (JwtAuthenticationToken) ctx.getAuthentication())
                .flatMap(auth -> {
                    Jwt jwt = (Jwt) auth.getCredentials();

                    ServerWebExchange mutated = exchange.mutate()
                            .request(r -> r.headers(headers -> {
                                headers.set("X-User-Id",
                                        jwt.getSubject());
                                headers.set("X-User-Email",
                                        jwt.getClaimAsString("email"));
                                headers.set("X-User-Roles",
                                        jwt.getClaimAsStringList("roles").toString());
                            }))
                            .build();

                    return chain.filter(mutated);
                });
    }

    @Override
    public int getOrder() { return -1; }
}