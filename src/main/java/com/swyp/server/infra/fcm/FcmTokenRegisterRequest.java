package com.swyp.server.infra.fcm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FcmTokenRegisterRequest(@NotBlank String token, @NotNull Platform platform) {}
