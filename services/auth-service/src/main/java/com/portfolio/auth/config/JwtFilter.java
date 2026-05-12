package com.portfolio.auth.config;

import com.portfolio.auth.service.JwtBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtBlacklistService jwtBlacklistService;

    public JwtFilter(JwtUtil jwtUtil,
                     JwtBlacklistService jwtBlacklistService) {

        this.jwtUtil = jwtUtil;
        this.jwtBlacklistService = jwtBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Skip JWT validation for OAuth endpoints
        if (path.startsWith("/oauth2") ||
                path.startsWith("/login/oauth2")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Skip JWT validation for public endpoints
        if (path.equals("/api/auth/login") ||
                path.equals("/api/auth/register") ||
                path.equals("/api/auth/refresh") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs")) {

            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            // Validate JWT
            if (jwtUtil.validateToken(token)) {

                // Extract jti
                String jti = jwtUtil.extractJti(token);

                // Check blacklist
                if (jwtBlacklistService.isBlacklisted(jti)) {

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                    response.getWriter().write("Token is blacklisted");

                    return;
                }

                // Extract user email
                String email = jwtUtil.extractEmail(token);

                // Default role
                String role = "ROLE_USER";

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                token,
                                java.util.List.of(
                                        new org.springframework.security.core.authority.SimpleGrantedAuthority(role)
                                )
                        );

                auth.setDetails(email);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }}