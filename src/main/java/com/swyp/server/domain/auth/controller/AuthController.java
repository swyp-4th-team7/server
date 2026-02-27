package com.swyp.server.domain.auth.controller;

import com.swyp.server.domain.auth.dto.LoginResponse;
import com.swyp.server.domain.auth.dto.SocialLoginRequest;
import com.swyp.server.domain.auth.service.GoogleAuthService;
import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleAuthService googleAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> googleLogin(
            @Valid @RequestBody SocialLoginRequest request) {
        LoginResponse response = googleAuthService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "로그아웃")
    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal Long userId) {
        googleAuthService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<LoginResponse>> reissue(
            @RequestHeader("Authorization") String refreshToken) {
        LoginResponse response = googleAuthService.reissue(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
