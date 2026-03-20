package com.swyp.server.domain.family.dto;

public record FamilyMemberDashboardResponse(
        Long userId, String nickname, boolean todoCompleted, boolean habitCompleted) {}
