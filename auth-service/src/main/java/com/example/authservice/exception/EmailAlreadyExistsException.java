package com.example.authservice.exception;

public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException(){
        super("Пользователь с таким Email уже существует");
    }

}
