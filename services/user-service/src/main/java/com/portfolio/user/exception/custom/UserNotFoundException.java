package com.portfolio.user.exception.custom;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
    }
}
