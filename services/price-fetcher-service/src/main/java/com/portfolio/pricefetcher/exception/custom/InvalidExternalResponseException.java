package com.portfolio.pricefetcher.exception.custom;

public class InvalidExternalResponseException extends RuntimeException {

    public InvalidExternalResponseException(String message) {
        super(message);
    }
}