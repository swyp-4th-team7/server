package com.swyp.server.domain.auth.dto;

public record LoginResponse(String accessToken, String refreshToken, boolean isNewUser) {

    public static LoginResponse of(String accessToken, String refreshToken, boolean isNewUser) {
        return new LoginResponse(accessToken, refreshToken, isNewUser);
    }
}
