package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;
import java.util.List;

public record FailedHabitListResponse(List<FailedHabitResponse> failedHabits) {
    public static FailedHabitListResponse from(List<Habit> habits) {
        return new FailedHabitListResponse(habits.stream().map(FailedHabitResponse::from).toList());
    }
}
