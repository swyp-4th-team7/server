package com.swyp.server.infra.fcm;

import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Fcm", description = "FCM 토큰 관리 API")
@RestController
@RequestMapping("/api/v1/users/me/fcm/tokens")
@RequiredArgsConstructor
public class FcmTokenController {
    private final FcmTokenService fcmTokenService;

    @Operation(summary = "FCM 토큰 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> register(
            @RequestHeader("X-USER-ID") Long userId,
            @Valid @RequestBody FcmTokenRegisterRequest request) {

        fcmTokenService.registerFcmToken(userId, request.token(), request.platform());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "FCM 토큰 삭제")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-USER-ID") Long userId, @RequestParam String token) {
        fcmTokenService.delete(userId, token);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
