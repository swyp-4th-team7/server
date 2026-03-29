package com.swyp.server.domain.growth.dto;

public record ChildGrowthResponse(
        Long childId, String nickname, int todoStarCount, int habitStarCount, String weekRange) {}
