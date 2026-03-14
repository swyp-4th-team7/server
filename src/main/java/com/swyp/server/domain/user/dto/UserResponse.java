package com.swyp.server.domain.user.dto;

import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.entity.UserType;

public record UserResponse(
        Long id,
        String email,
        String nickname,
        UserType userType,
        boolean profileCompleted,
        boolean termsAgreed) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getUserType(),
                user.isProfileCompleted(),
                user.isTermsAgreed());
    }
}
