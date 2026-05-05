package com.tokki.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    AUTH_REQUIRED(401, "AUTH_REQUIRED", "인증이 필요합니다"),
    PERMISSION_DENIED(403, "PERMISSION_DENIED", "권한이 없습니다"),
    NOT_FOUND(404, "NOT_FOUND", "리소스를 찾을 수 없습니다"),
    ALREADY_EXISTS(409, "ALREADY_EXISTS", "이미 존재하는 데이터입니다"),
    INVALID_ARGUMENT(400, "INVALID_ARGUMENT", "잘못된 입력값입니다"),
    FILE_TOO_LARGE(413, "FILE_TOO_LARGE", "파일 크기는 5MB 이하여야 합니다"),
    INVALID_FILE_FORMAT(415, "INVALID_FILE_FORMAT", ".xlsx 파일만 허용됩니다"),
    INVITE_CODE_NOT_FOUND(404, "INVITE_CODE_NOT_FOUND", "초대 코드를 찾을 수 없습니다"),
    ROOM_FULL(409, "ROOM_FULL", "이미 입장한 방입니다"),
    INTERNAL(500, "INTERNAL", "서버 오류가 발생했습니다");

    private final int status;
    private final String code;
    private final String message;
}
