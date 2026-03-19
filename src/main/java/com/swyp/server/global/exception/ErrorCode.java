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
    USER_NOT_FOUND(40401, 404, "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(40901, 409, "이미 사용 중인 이메일입니다."),

    // Todos
    TODO_NOT_FOUND(40402, 404, "할 일을 찾을 수 없습니다."),

    // FCM
    FCM_SEND_FAILED(50001, 500, "푸시 알림 전송에 실패했습니다."),
    FCM_TOPIC_SUBSCRIBE_FAILED(50002, 500, "푸시 토픽 구독에 실패했습니다."),
    FCM_TOPIC_UNSUBSCRIBE_FAILED(50003, 500, "푸시 알림 구독 해제에 실패했습니다."),

    // Validation
    FCM_TOKEN_REQUIRED(40001, 400, "FCM 토큰은 필수입니다."),
    PLATFORM_REQUIRED(40002, 400, "플랫폼은 필수입니다."),

    SOCIAL_TYPE_REQUIRED(40003, 400, "소셜 타입은 필수입니다."),
    SOCIAL_TOKEN_REQUIRED(40004, 400, "소셜 토큰은 필수입니다."),

    NICKNAME_REQUIRED(40005, 400, "닉네임은 필수입니다."),
    NICKNAME_LENGTH_INVALID(40006, 400, "닉네임은 1자 이상 12자 이하여야 합니다."),
    NICKNAME_PATTERN_INVALID(40007, 400, "닉네임은 특수문자를 사용할 수 없습니다."),
    USER_TYPE_REQUIRED(40008, 400, "사용자 유형은 필수입니다."),

    TODO_TITLE_REQUIRED(40009, 400, "할 일 제목은 필수입니다."),
    TODO_CATEGORY_REQUIRED(40011, 400, "할 일 카테고리는 필수입니다."),
    TODO_COLOR_REQUIRED(40013, 400, "할 일 색상은 필수입니다."),
    TODO_CATEGORY_INVALID(40018, 400, "유효하지 않은 할 일 카테고리 입니다."),

    // Schedule
    SCHEDULE_NOT_FOUND(40403, 404, "일정을 찾을 수 없습니다."),

    // Validation
    SCHEDULE_TITLE_REQUIRED(40014, 400, "일정 제목은 필수입니다."),
    SCHEDULE_CATEGORY_REQUIRED(40016, 400, "일정 카테고리는 필수입니다."),
    SCHEDULE_DATE_REQUIRED(40017, 400, "일정 날짜는 필수입니다."),

    // Habit
    HABIT_NOT_FOUND(40405, 404, "습관을 찾을 수 없습니다."),

    HABIT_TITLE_REQUIRED(40019, 400, "습관 제목은 필수입니다."),
    HABIT_DURATION_REQUIRED(40020, 400, "습관 기간 설정은 필수입니다."),
    HABIT_REWARD_REQUIRED(40021, 400, "습관 보상 설정은 필수입니다."),
    HABIT_COMPLETED_REQUIRED(40022, 400, "습관 완료 여부는 필수입니다.");

    private final int code;
    private final int status;
    private final String message;

    // Validation annotation message에 설정한 key를 ErrorCode로 변환
    public static ErrorCode fromValidationKey(String key) {
        if (key == null || key.isBlank()) {
            return INVALID_INPUT_VALUE;
        }

        try {
            return ErrorCode.valueOf(key.trim());
        } catch (IllegalArgumentException e) {
            return INVALID_INPUT_VALUE;
        }
    }
}
