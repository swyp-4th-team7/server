package com.swyp.server.infra.fcm.dto;

import com.swyp.server.infra.fcm.entity.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FcmTokenRegisterRequest(
        @NotBlank(message = "FCM_TOKEN_REQUIRED") String token,
        @NotNull(message = "PLATFORM_REQUIRED") Platform platform) {}
