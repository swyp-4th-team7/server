package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.HabitDuration;
import jakarta.validation.constraints.NotNull;

public record HabitRetryRequest(
        @NotNull(message = "HABIT_DURATION_REQUIRED") HabitDuration duration, String reward) {}
