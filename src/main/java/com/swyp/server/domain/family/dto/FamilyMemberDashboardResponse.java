package com.swyp.server.domain.family.dto;

public record FamilyMemberDashboardResponse(
        Long userId, String nickname, TodoSummary todo, HabitSummary habit) {}
