package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;

public record ParentHabitResponse(
        Long habitId, String title, String duration, boolean isCompleted) {

    public static ParentHabitResponse from(Habit habit){
        return new ParentHabitResponse(
                habit.getId(),
                habit.getTitle(),
                habit.getDuration().getLabel(),
                habit.isCompleted());
    }
}

