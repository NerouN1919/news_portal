package com.portal.news.Errors;

public class Failed extends RuntimeException{

    public Failed(String message) {
        super(message);
    }
}
