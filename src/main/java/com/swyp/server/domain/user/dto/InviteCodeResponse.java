package com.swyp.server.domain.user.dto;

import com.swyp.server.domain.user.entity.User;

public record InviteCodeResponse(String inviteCode) {
    public static InviteCodeResponse from(User user) {
        return new InviteCodeResponse(user.getInviteCode());
    }
}
