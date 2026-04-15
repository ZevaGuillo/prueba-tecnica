package com.zevaguillo.application.exception;

public class ClienteYaExisteException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public ClienteYaExisteException(String message) {
        super(message);
    }
}
