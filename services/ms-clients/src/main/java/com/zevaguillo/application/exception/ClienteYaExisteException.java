package com.zevaguillo.application.exception;

public class ClienteYaExisteException extends RuntimeException {
    public ClienteYaExisteException(String message) {
        super(message);
    }
}
