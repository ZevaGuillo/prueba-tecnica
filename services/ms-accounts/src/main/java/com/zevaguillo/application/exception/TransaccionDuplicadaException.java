package com.zevaguillo.application.exception;

public class TransaccionDuplicadaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TransaccionDuplicadaException(String message) {
        super(message);
    }
}
