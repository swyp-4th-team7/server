package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.user.entity.UserType;

public record HabitResponse(
        Long habitId, String title, String duration, String reward, boolean isCompleted) {

    public static HabitResponse from(Habit habit){

        String reward = (habit.getUser().getUserType().equals(UserType.PARENT)) ? null : habit.getReward();

        return new HabitResponse(
                habit.getId(),
                habit.getTitle(),
                habit.getDuration().getLabel(),
                reward,
                habit.isCompleted());
    }
}

