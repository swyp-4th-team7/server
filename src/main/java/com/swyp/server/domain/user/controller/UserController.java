package com.swyp.server.domain.user.controller;

import com.swyp.server.domain.user.dto.ProfileRequest;
import com.swyp.server.domain.user.service.UserService;
import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "유저 프로필 API")
@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "내 프로필 설정")
    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @AuthenticationPrincipal Long userId, @Valid @RequestBody ProfileRequest request) {
        userService.updateProfile(userId, request.nickname(), request.userType());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
