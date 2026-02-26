package com.swyp.server.domain.user.dto;

public record LoginResponse(String accessToken, String refreshToken, boolean isNewUser) {}
