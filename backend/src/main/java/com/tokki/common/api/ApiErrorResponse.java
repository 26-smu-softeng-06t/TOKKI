package com.tokki.common.api;

public record ApiErrorResponse(ApiError error) {

    public record ApiError(String code, String message) {
    }
}
