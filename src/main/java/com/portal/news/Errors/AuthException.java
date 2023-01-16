package com.portal.news.Errors;

public class AuthException extends RuntimeException{

    public AuthException(String message) {
        super(message);
    }
}