package com.portfolio.auth.security;

import com.portfolio.auth.config.JwtUtil;
import com.portfolio.auth.entity.User;
import com.portfolio.auth.entity.Role;

import com.portfolio.auth.enums.AuthProvider;

import com.portfolio.auth.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuth2SuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    public OAuth2SuccessHandler(
            UserRepository userRepository,
            JwtUtil jwtUtil
    ) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();

        String email =
                oauthUser.getAttribute("email");

        String name =
                oauthUser.getAttribute("name");

        String providerId =
                oauthUser.getAttribute("sub");

        User user =
                userRepository.findByEmail(email)
                        .orElseGet(() -> {

                            User newUser = new User();

                            newUser.setEmail(email);

                            newUser.setFullName(name);

                            newUser.setPassword(
                                    UUID.randomUUID().toString()
                            );

                            newUser.setRole(Role.USER);

                            newUser.setProvider(
                                    AuthProvider.GOOGLE
                            );

                            newUser.setProviderId(providerId);

                            return userRepository.save(newUser);
                        });

        // Generate JWT
        String token =
                jwtUtil.generateToken(user.getEmail());

        // Redirect with token
        response.setContentType("application/json");

        response.setCharacterEncoding("UTF-8");

        response.setContentType("application/json");

        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                "{\"accessToken\":\"" + token + "\"}"
        );

        response.getWriter().flush();
    }
}