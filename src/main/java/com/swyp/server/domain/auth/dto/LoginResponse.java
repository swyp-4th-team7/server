package com.swyp.server.domain.auth.dto;

import com.swyp.server.domain.user.entity.UserType;

public record LoginResponse(
        String accessToken, String refreshToken, UserType userType, boolean profileCompleted) {}
