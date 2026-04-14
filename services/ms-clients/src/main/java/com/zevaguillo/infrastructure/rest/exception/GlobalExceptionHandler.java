package com.zevaguillo.infrastructure.rest.exception;

import com.zevaguillo.application.exception.ClienteYaExisteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Pattern IDENTIFICACION_PATTERN = Pattern.compile("\\(identificacion\\)=\\(([^)]+)\\)");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation Failed",
                "message", errors
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(ClienteYaExisteException.class)
    public ResponseEntity<Map<String, Object>> handleClienteYaExiste(ClienteYaExisteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.CONFLICT.value(),
                "error", "Cliente Duplicado",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String rootMessage = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        if (rootMessage != null && rootMessage.contains("fk_cliente_persona")) {
            String identificacion = extractIdentificacion(rootMessage);
            String message = identificacion != null
                    ? "No se puede crear/actualizar el cliente: la persona con identificacion '" + identificacion + "' no existe."
                    : "No se puede crear/actualizar el cliente: la persona asociada no existe.";

            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "status", HttpStatus.CONFLICT.value(),
                    "error", "Foreign Key Violation",
                    "message", message
            ));
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.CONFLICT.value(),
                "error", "Data Integrity Violation",
                "message", "La operación viola una restricción de integridad de datos."
        ));
    }

    private String extractIdentificacion(String message) {
        Matcher matcher = IDENTIFICACION_PATTERN.matcher(message);
        return matcher.find() ? matcher.group(1) : null;
    }
}