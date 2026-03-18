package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;

import java.util.List;

public record ParentHabitListResponse(List<ParentHabitResponse> habits) {
    public static ParentHabitListResponse from(List<Habit> habits){
        return new ParentHabitListResponse(habits.stream().map(ParentHabitResponse::from).toList());
    }
}
