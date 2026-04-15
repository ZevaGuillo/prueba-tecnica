package com.zevaguillo.application.exception;

public class ClienteNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public ClienteNoEncontradoException(String message) {
        super(message);
    }
}
