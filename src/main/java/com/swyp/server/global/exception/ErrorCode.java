package com.swyp.server.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(40000, 400, "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(50000, 500, "서버 오류가 발생했습니다."),
    DATA_INTEGRITY_VIOLATION(40900, 409, "요청을 처리할 수 없습니다."),

    // Auth
    UNAUTHORIZED(40100, 401, "인증이 필요합니다."),
    INVALID_TOKEN(40101, 401, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(40102, 401, "만료된 토큰입니다."),
    FORBIDDEN(40300, 403, "접근 권한이 없습니다."),

    // User
    USER_NOT_FOUND(40400, 404, "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(40900, 409, "이미 사용 중인 이메일입니다."),

    // FCM
    FCM_SEND_FAILED(50001, 500, "푸시 알림 전송에 실패했습니다."),
    FCM_TOPIC_SUBSCRIBE_FAILED(50002, 500, "푸시 토픽 구독에 실패했습니다."),
    FCM_TOPIC_UNSUBSCRIBE_FAILED(50003, 500, "푸시 알림 구독 해제에 실패했습니다.");

    private final int code;
    private final int status;
    private final String message;
}
