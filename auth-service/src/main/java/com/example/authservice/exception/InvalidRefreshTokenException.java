package com.example.authservice.exception;

public class InvalidRefreshTokenException extends RuntimeException{
    InvalidRefreshTokenException(String message){
        super(message);
    }
}
