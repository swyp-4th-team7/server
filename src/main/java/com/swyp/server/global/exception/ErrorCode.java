package com.swyp.server.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(40000, "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(50000, "서버 오류가 발생했습니다."),

    // Auth
    UNAUTHORIZED(40100, "인증이 필요합니다."),
    INVALID_TOKEN(40101, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(40102, "만료된 토큰입니다."),
    FORBIDDEN(40300, "접근 권한이 없습니다."),

    // User
    USER_NOT_FOUND(40400, "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(40900, "이미 사용 중인 이메일입니다.");

    private final int code;
    private final String message;
}
