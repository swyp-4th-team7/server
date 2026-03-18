package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;

public record ChildHabitResponse(
        Long habitId, String title, String duration, String reward, boolean isCompleted) {

    public static ChildHabitResponse from(Habit habit){
        return new ChildHabitResponse(
                habit.getId(),
                habit.getTitle(),
                habit.getDuration().getLabel(),
                habit.getReward(),
                habit.isCompleted());
    }
}

