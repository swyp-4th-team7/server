package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.HabitDuration;
import com.swyp.server.domain.user.entity.UserType;

public record HabitRewardDetailResponse(
        Long habitId, String title, HabitDuration duration, String reward) {

    public static HabitRewardDetailResponse from(Habit habit) {

        String reward =
                (habit.getUser().getUserType().equals(UserType.PARENT)) ? null : habit.getReward();

        return new HabitRewardDetailResponse(
                habit.getId(), habit.getTitle(), habit.getDuration(), reward);
    }
}
