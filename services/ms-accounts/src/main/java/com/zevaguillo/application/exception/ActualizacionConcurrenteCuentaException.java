package com.zevaguillo.application.exception;

public class ActualizacionConcurrenteCuentaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ActualizacionConcurrenteCuentaException(String message, Throwable cause) {
        super(message, cause);
    }
}
