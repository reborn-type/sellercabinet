package com.example.authservice.exception;

public class InvalidCredentialsException extends RuntimeException{

    public InvalidCredentialsException(){
        super("Неверный email или пароль");
    }
}
