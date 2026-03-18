package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.HabitDuration;

public record ParentHabitCreateResponse(
        Long habitId, String title, HabitDuration duration, boolean isCompleted) {

    public static ParentHabitCreateResponse from(Habit habit){
        return new ParentHabitCreateResponse(
                habit.getId(),
                habit.getTitle(),
                habit.getDuration(),
                habit.isCompleted());
    }

}
