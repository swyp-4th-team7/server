package com.swyp.server.domain.family.dto;

import com.swyp.server.domain.user.entity.User;

public record ConnectedMemberResponse(Long userId, String nickname) {

    public static ConnectedMemberResponse from(User user) {
        return new ConnectedMemberResponse(user.getId(), user.getNickname());
    }
}
