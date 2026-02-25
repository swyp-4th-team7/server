package com.swyp.server.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.swyp.server.global.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.error("CustomException: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getCode() / 100)
                .body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e) {
        // 우선순위 높은 에러 하나만 반환
        String message =
                e.getBindingResult().getFieldErrors().stream()
                        .findFirst()
                        .map(fieldError -> fieldError.getDefaultMessage())
                        .orElse("입력값이 올바르지 않습니다.");
        return ResponseEntity.status(400)
                .body(ApiResponse.fail(ErrorCode.INVALID_INPUT_VALUE.getCode(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage());
        return ResponseEntity.status(500)
                .body(
                        ApiResponse.fail(
                                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                                ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
