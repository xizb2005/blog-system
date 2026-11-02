package com.example.demo3.common.exception;

public class ThirdPartyApiException extends RuntimeException{
    public ThirdPartyApiException(String message) {
        super(message);
    }
}