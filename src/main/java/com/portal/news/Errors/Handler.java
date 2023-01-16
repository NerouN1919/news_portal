package com.portal.news.Errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Handler {
    @ExceptionHandler
    public ResponseEntity<Info> handle(Failed exception){
        Info info = new Info();
        info.setInfo(exception.getMessage());
        return new ResponseEntity<>(info, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<Info> handle(AuthException exception){
        Info info = new Info();
        info.setInfo(exception.getMessage());
        return new ResponseEntity<>(info, HttpStatus.UNAUTHORIZED);
    }
}
