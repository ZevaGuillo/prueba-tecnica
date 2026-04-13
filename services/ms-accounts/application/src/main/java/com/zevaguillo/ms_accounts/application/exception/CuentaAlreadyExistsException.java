package com.zevaguillo.ms_accounts.application.exception;

public class CuentaAlreadyExistsException extends RuntimeException {
    public CuentaAlreadyExistsException(String message) {
        super(message);
    }
}