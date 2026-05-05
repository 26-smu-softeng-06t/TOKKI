package com.tokki.common.api;

public final class ApiResponses {

    private ApiResponses() {
    }

    public static <T> ApiResponse<T> data(T value) {
        return new ApiResponse<>(value);
    }

    public static ApiErrorResponse error(String code, String message) {
        return new ApiErrorResponse(new ApiErrorResponse.ApiError(code, message));
    }
}
