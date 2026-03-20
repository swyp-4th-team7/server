package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.RewardStatus;
import jakarta.validation.constraints.NotNull;

public record HabitRewardUpdateRequest(
        @NotNull(message = "REWARD_STATUS_REQUIRED") RewardStatus rewardStatus,
        @NotNull(message = "HABIT_REWARD_REQUIRED") String reward) {}
