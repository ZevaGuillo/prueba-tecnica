package com.zevaguillo.infrastructure.rest.exception;

import com.zevaguillo.application.exception.ActualizacionConcurrenteCuentaException;
import com.zevaguillo.application.exception.ClienteNoEncontradoException;
import com.zevaguillo.application.exception.CuentaAlreadyExistsException;
import com.zevaguillo.application.exception.CuentaConSaldoActivoException;
import com.zevaguillo.application.exception.TransaccionDuplicadaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String JSON_TIMESTAMP = "timestamp";
    private static final String JSON_STATUS = "status";
    private static final String JSON_ERROR = "error";
    private static final String JSON_MESSAGE = "message";

    private static final String ERR_CONFLICT = "Conflict";

    private static Map<String, Object> jsonError(int statusValue, String error, String message) {
        return Map.of(
                JSON_TIMESTAMP, LocalDateTime.now().toString(),
                JSON_STATUS, statusValue,
                JSON_ERROR, error,
                JSON_MESSAGE, message
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                jsonError(HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors));
    }

    @ExceptionHandler(CuentaAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCuentaAlreadyExists(CuentaAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                jsonError(HttpStatus.CONFLICT.value(), ERR_CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(CuentaConSaldoActivoException.class)
    public ResponseEntity<Map<String, Object>> handleCuentaConSaldoActivo(CuentaConSaldoActivoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                jsonError(HttpStatus.CONFLICT.value(), ERR_CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(ActualizacionConcurrenteCuentaException.class)
    public ResponseEntity<Map<String, Object>> handleActualizacionConcurrenteCuenta(
            ActualizacionConcurrenteCuentaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                jsonError(HttpStatus.CONFLICT.value(), ERR_CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(TransaccionDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> handleTransaccionDuplicada(TransaccionDuplicadaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                jsonError(HttpStatus.CONFLICT.value(), ERR_CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleClienteNoEncontrado(ClienteNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                jsonError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                jsonError(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage()));
    }
}
