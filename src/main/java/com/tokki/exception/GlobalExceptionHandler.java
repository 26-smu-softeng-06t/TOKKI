package com.tokki.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppException(AppException e) {
        log.warn("AppException [{}]: {}", e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Map.of("error", e.getErrorCode().name(), "message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fields = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err ->
                fields.put(((FieldError) err).getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(Map.of("error", "VALIDATION_FAILED", "fields", fields));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.internalServerError()
                .body(Map.of("error", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다."));
    }
}
