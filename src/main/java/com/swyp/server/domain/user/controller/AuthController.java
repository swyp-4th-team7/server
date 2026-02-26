package com.swyp.server.domain.user.controller;

import com.swyp.server.domain.user.dto.GoogleLoginRequest;
import com.swyp.server.domain.user.dto.LoginResponse;
import com.swyp.server.domain.user.service.GoogleAuthService;
import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleAuthService googleAuthService;

    @Operation(summary = "구글 소셜 로그인")
    @PostMapping("/google")
    public ResponseEntity<ApiResponse<LoginResponse>> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request) {
        LoginResponse response = googleAuthService.googleLogin(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
