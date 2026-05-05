package com.tokki.exception;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppException(AppException ex) {
        return error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof SQLIntegrityConstraintViolationException) {
            String message = cause.getMessage() == null ? "" : cause.getMessage().toLowerCase();
            if (message.contains("duplicate")) {
                return error(ErrorCode.ALREADY_EXISTS, ErrorCode.ALREADY_EXISTS.getMessage());
            }
            if (message.contains("foreign key")) {
                return error(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
            }
        }
        return error(ErrorCode.INTERNAL, ErrorCode.INTERNAL.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(FieldError::getDefaultMessage)
            .orElse(ErrorCode.INVALID_ARGUMENT.getMessage());
        return error(ErrorCode.INVALID_ARGUMENT, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        return error(ErrorCode.INTERNAL, ErrorCode.INTERNAL.getMessage());
    }

    private ResponseEntity<Map<String, Object>> error(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
            .body(Map.of("error", Map.of("code", errorCode.getCode(), "message", message)));
    }
}
