package com.swyp.server.domain.user.dto;

import com.swyp.server.domain.user.entity.User;

public record UserResponse(Long id, String email, String nickname, String profileImageUrl) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(), user.getEmail(), user.getNickname(), user.getProfileImageUrl());
    }
}
