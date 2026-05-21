package com.tokki.exception;

import com.tokki.common.api.ApiErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        ReflectionTestUtils.setField(handler, "maxUploadSize", "5MB");
    }

    @Test
    void appException_returnsNestedErrorFormat() {
        AppException ex = new AppException(ErrorCode.STAGE_NOT_FOUND);

        ResponseEntity<ApiErrorResponse> response = handler.handleAppException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().code()).isEqualTo("STAGE_NOT_FOUND");
        assertThat(response.getBody().error().message()).isEqualTo(ErrorCode.STAGE_NOT_FOUND.getMessage());
    }

    @Test
    void appException_customMessage_returnsCustomMessage() {
        AppException ex = new AppException(ErrorCode.INVALID_INPUT, "stageNumber: 1 이상이어야 합니다");

        ResponseEntity<ApiErrorResponse> response = handler.handleAppException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error().code()).isEqualTo("INVALID_INPUT");
        assertThat(response.getBody().error().message()).isEqualTo("stageNumber: 1 이상이어야 합니다");
    }

    @Test
    void validation_returnsFirstFieldErrorInNestedFormat() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "difficulty", "must not be null"));
        bindingResult.addError(new FieldError("target", "stageNumber", "must be between 1 and 10"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiErrorResponse> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error().code()).isEqualTo("VALIDATION_FAILED");
        assertThat(response.getBody().error().message()).isEqualTo("difficulty: must not be null");
    }

    @Test
    void illegalArgument_returnsInvalidInputInNestedFormat() {
        IllegalArgumentException ex = new IllegalArgumentException("Unsupported difficulty: low");

        ResponseEntity<ApiErrorResponse> response = handler.handleInvalidInput(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error().code()).isEqualTo("INVALID_INPUT");
        assertThat(response.getBody().error().message()).isEqualTo(ErrorCode.INVALID_INPUT.getMessage());
    }

    @Test
    void methodArgumentTypeMismatch_returnsInvalidInputInNestedFormat() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Integer.class, "stageId", null, new NumberFormatException("abc"));

        ResponseEntity<ApiErrorResponse> response = handler.handleInvalidInput(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error().code()).isEqualTo("INVALID_INPUT");
    }

    @Test
    void maxUploadSizeExceeded_returnsFileSizeMessageInNestedFormat() {
        org.springframework.web.multipart.MaxUploadSizeExceededException ex =
                new org.springframework.web.multipart.MaxUploadSizeExceededException(5 * 1024 * 1024);

        ResponseEntity<ApiErrorResponse> response = handler.handleMaxUploadSizeExceeded(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error().code()).isEqualTo("INVALID_INPUT");
        assertThat(response.getBody().error().message()).contains("5MB");
    }

    @Test
    void unexpectedException_returns500InNestedFormat() {
        RuntimeException ex = new RuntimeException("DB connection lost");

        ResponseEntity<ApiErrorResponse> response = handler.handleException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().error().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().error().message()).isEqualTo("서버 오류가 발생했습니다.");
    }

    @Test
    void allHandlers_errorFieldIsObject_notString() {
        AppException ex = new AppException(ErrorCode.UNAUTHORIZED);
        ResponseEntity<ApiErrorResponse> response = handler.handleAppException(ex);

        // error 필드가 중첩 객체여야 함 — String이면 프론트에서 error.code 접근 불가
        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.error()).isInstanceOf(ApiErrorResponse.ApiError.class);
        assertThat(body.error().code()).isNotBlank();
        assertThat(body.error().message()).isNotBlank();
    }
}
