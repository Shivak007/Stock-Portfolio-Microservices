package com.portfolio.auth.mapper;

import com.portfolio.auth.dto.RegisterRequestDto;
import com.portfolio.auth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequestDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        return user;
    }
}