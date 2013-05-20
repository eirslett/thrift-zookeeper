package com.example.myapp.commons;

public class ClientCreationException extends RuntimeException {
    public ClientCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
