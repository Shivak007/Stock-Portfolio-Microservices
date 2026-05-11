package com.portfolio.pricefetcher.exception.custom;

public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(message);
    }
}