package com.swyp.server.global.exception;

import static com.swyp.server.global.exception.ErrorCode.FORBIDDEN;
import static com.swyp.server.global.exception.ErrorCode.UNAUTHORIZED;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.server.global.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {

        write(
                response,
                UNAUTHORIZED.getStatus(),
                ApiResponse.fail(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage()));
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        write(
                response,
                FORBIDDEN.getStatus(),
                ApiResponse.fail(FORBIDDEN.getCode(), FORBIDDEN.getMessage()));
    }

    private void write(HttpServletResponse response, int httpStatus, ApiResponse<Void> body)
            throws IOException {
        response.setStatus(httpStatus);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), body);
    }
}
