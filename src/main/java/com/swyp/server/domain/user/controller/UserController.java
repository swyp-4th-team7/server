package com.swyp.server.domain.user.controller;

import com.swyp.server.domain.user.dto.ProfileRequest;
import com.swyp.server.domain.user.dto.UserResponse;
import com.swyp.server.domain.user.service.UserService;
import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "유저 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getMe(userId)));
    }

    @Operation(summary = "내 프로필 설정")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @AuthenticationPrincipal Long userId, @Valid @RequestBody ProfileRequest request) {
        userService.updateProfile(userId, request.nickname(), request.userType());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "회원탈퇴")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(@AuthenticationPrincipal Long userId) {
        userService.withdraw(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "이용약관 동의")
    @PatchMapping("/me/terms")
    public ResponseEntity<ApiResponse<Void>> agreeToTerms(@AuthenticationPrincipal Long userId) {
        userService.agreeToTerms(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
