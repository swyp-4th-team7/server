package com.swyp.server.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(@NotBlank String socialType, @NotBlank String token) {}
