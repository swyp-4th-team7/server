package com.swyp.server.domain.habit.dto;

import jakarta.validation.constraints.NotNull;

public record HabitRewardUpdateRequest(@NotNull(message = "HABIT_REWARD_REQUIRED") String reward) {}
