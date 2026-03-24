package com.swyp.server.global.exception;

import static com.swyp.server.global.exception.ErrorCode.DATA_INTEGRITY_VIOLATION;
import static com.swyp.server.global.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.swyp.server.global.exception.ErrorCode.INVALID_INPUT_VALUE;

import com.swyp.server.global.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
        FieldError fieldError =
                e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);

        ErrorCode errorCode = INVALID_INPUT_VALUE;

        if (fieldError != null) {
            errorCode = ErrorCode.fromValidationKey(fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException e) {
        // 우선순위 높은 에러 하나만 반환
        ConstraintViolation<?> constraintViolation =
                e.getConstraintViolations().stream().findFirst().orElse(null);

        ErrorCode errorCode = INVALID_INPUT_VALUE;

        if (constraintViolation != null) {
            errorCode = ErrorCode.fromValidationKey(constraintViolation.getMessage());
        }

        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(INVALID_INPUT_VALUE.getStatus())
                .body(
                        ApiResponse.fail(
                                INVALID_INPUT_VALUE.getCode(), INVALID_INPUT_VALUE.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        log.warn("MissingServletRequestParameterException: {}", e.getParameterName());

        return ResponseEntity.status(INVALID_INPUT_VALUE.getStatus())
                .body(
                        ApiResponse.fail(
                                INVALID_INPUT_VALUE.getCode(), INVALID_INPUT_VALUE.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadableException(
            HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException", e);

        return ResponseEntity.status(INVALID_INPUT_VALUE.getStatus())
                .body(
                        ApiResponse.fail(
                                INVALID_INPUT_VALUE.getCode(), INVALID_INPUT_VALUE.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleIntegrityViolationException(
            DataIntegrityViolationException e) {
        log.warn("DataIntegrityViolationException", e);

        return ResponseEntity.status(DATA_INTEGRITY_VIOLATION.getStatus())
                .body(
                        ApiResponse.fail(
                                DATA_INTEGRITY_VIOLATION.getCode(),
                                DATA_INTEGRITY_VIOLATION.getMessage()));
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
