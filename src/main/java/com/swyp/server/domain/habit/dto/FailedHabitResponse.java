package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.HabitDuration;
import com.swyp.server.domain.user.entity.UserType;

public record FailedHabitResponse(
        Long habitId, String title, HabitDuration duration, String reward) {

    public static FailedHabitResponse from(Habit habit) {

        String reward =
                (habit.getUser().getUserType() == (UserType.PARENT)) ? null : habit.getReward();

        return new FailedHabitResponse(
                habit.getId(), habit.getTitle(), habit.getDuration(), reward);
    }
}
