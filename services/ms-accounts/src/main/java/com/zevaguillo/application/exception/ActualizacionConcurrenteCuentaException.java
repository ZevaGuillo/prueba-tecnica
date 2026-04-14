package com.zevaguillo.application.exception;

public class ActualizacionConcurrenteCuentaException extends RuntimeException {

    public ActualizacionConcurrenteCuentaException(String message, Throwable cause) {
        super(message, cause);
    }
}
