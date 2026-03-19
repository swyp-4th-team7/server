package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;

import java.util.List;

public record HabitListResponse(List<ChildHabitResponse> habits) {
    public static HabitListResponse from(List<Habit> habits){
        return new HabitListResponse(habits.stream().map(ChildHabitResponse::from).toList());
    }
}
