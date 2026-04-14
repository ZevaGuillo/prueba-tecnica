package com.zevaguillo.application.exception;

public class TransaccionDuplicadaException extends RuntimeException {

    public TransaccionDuplicadaException(String message) {
        super(message);
    }
}
