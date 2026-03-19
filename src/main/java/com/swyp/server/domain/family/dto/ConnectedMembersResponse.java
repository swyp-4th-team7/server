package com.swyp.server.domain.family.dto;

import com.swyp.server.domain.user.entity.User;
import java.util.List;

public record ConnectedMembersResponse(List<ConnectedMemberResponse> members) {
    public static ConnectedMembersResponse from(List<User> users) {
        return new ConnectedMembersResponse(
                users.stream().map(ConnectedMemberResponse::from).toList());
    }
}
