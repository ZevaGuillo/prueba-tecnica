package com.zevaguillo.application.exception;

public class CuentaAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public CuentaAlreadyExistsException(String message) {
        super(message);
    }
}