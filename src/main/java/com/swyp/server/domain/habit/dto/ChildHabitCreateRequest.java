package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.HabitDuration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChildHabitCreateRequest(
        @NotBlank(message = "HABIT_TITLE_REQUIRED") String title,
        @NotNull(message = "HABIT_DURATION_REQUIRED")HabitDuration duration,
        @NotBlank(message = "HABIT_REWARD_REQUIRED") String reward) {}
