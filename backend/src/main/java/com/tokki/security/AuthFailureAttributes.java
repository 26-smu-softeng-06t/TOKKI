package com.tokki.security;

import com.tokki.exception.ErrorCode;

public final class AuthFailureAttributes {

    public static final String ERROR_CODE = "tokki.auth.errorCode";

    private AuthFailureAttributes() {
    }

    public static ErrorCode resolve(Object value) {
        return value instanceof ErrorCode errorCode ? errorCode : ErrorCode.TOKEN_MISSING;
    }
}
