package com.swyp.server.infra.fcm.dto;

import com.swyp.server.infra.fcm.entity.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FcmTokenRegisterRequest(
        @NotBlank(message = "FCM 토큰은 필수입니다.") String token,
        @NotNull(message = "플랫폼은 필수입니다.") Platform platform) {}
