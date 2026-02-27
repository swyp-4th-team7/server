package com.swyp.server.domain.auth.dto;

public record LoginResponse(String accessToken, String refreshToken, boolean isNewUser) {}
