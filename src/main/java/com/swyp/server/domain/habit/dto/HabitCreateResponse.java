package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.HabitDuration;

public record HabitCreateResponse(
        Long habitId, String title, HabitDuration duration, String reward, boolean isCompleted) {

    public static HabitCreateResponse from(Habit habit) {
        return new HabitCreateResponse(
                habit.getId(),
                habit.getTitle(),
                habit.getDuration(),
                habit.getReward(),
                habit.isCompleted());
    }
}
