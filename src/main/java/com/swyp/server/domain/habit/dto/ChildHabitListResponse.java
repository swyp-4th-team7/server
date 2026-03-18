package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;

import java.util.List;

public record ChildHabitListResponse(List<ChildHabitResponse> habits) {
    public static ChildHabitListResponse from(List<Habit> habits){
        return new ChildHabitListResponse(habits.stream().map(ChildHabitResponse::from).toList());
    }
}
