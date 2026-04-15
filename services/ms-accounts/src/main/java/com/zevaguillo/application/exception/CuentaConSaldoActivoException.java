package com.zevaguillo.application.exception;

public class CuentaConSaldoActivoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CuentaConSaldoActivoException(String message) {
        super(message);
    }
}
