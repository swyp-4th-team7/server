package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.HabitDuration;

public record HabitRewardDetailResponse(
        Long habitId, String title, HabitDuration duration, String reward) {

    public static HabitRewardDetailResponse from(Habit habit) {

        return new HabitRewardDetailResponse(
                habit.getId(), habit.getTitle(), habit.getDuration(), habit.getReward());
    }
}
