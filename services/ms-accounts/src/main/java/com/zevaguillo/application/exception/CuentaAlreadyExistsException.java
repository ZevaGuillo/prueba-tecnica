package com.zevaguillo.application.exception;

public class CuentaAlreadyExistsException extends RuntimeException {
    public CuentaAlreadyExistsException(String message) {
        super(message);
    }
}