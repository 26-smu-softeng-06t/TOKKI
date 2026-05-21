package com.tokki.exception;

import com.tokki.common.api.ApiErrorResponse;
import com.tokki.common.api.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size:5MB}")
    private String maxUploadSize;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(AppException e) {
        log.warn("AppException [{}]: {}", e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ApiResponses.error(e.getErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse("유효하지 않은 입력값입니다.");
        return ResponseEntity.badRequest()
                .body(ApiResponses.error("VALIDATION_FAILED", message));
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiErrorResponse> handleInvalidInput(Exception e) {
        log.warn("Invalid input: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponses.error(ErrorCode.INVALID_INPUT.name(), ErrorCode.INVALID_INPUT.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("Max upload size exceeded: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponses.error(ErrorCode.INVALID_INPUT.name(), maxUploadSize + " 이하 파일만 업로드할 수 있습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.internalServerError()
                .body(ApiResponses.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
    }
}
