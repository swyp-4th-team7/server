package com.swyp.server.global.exception;

import static com.swyp.server.global.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.swyp.server.global.exception.ErrorCode.INVALID_INPUT_VALUE;

import com.swyp.server.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.info("CustomException: {}", e.getErrorCode().getCode());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e) {
        // 우선순위 높은 에러 하나만 반환
        String message =
                e.getBindingResult().getFieldErrors().stream()
                        .findFirst()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .orElse(INVALID_INPUT_VALUE.getMessage());
        return ResponseEntity.status(INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.fail(INVALID_INPUT_VALUE.getCode(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {

        log.error("Unhandled Exception", e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getStatus())
                .body(
                        ApiResponse.fail(
                                INTERNAL_SERVER_ERROR.getCode(),
                                INTERNAL_SERVER_ERROR.getMessage()));
    }
}
