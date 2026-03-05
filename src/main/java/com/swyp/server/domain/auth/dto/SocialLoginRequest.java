package com.swyp.server.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(
        @NotBlank(message = "SOCIAL_TYPE_REQUIRED") String socialType,
        @NotBlank(message = "SOCIAL_TOKEN_REQUIRED") String token) {}
