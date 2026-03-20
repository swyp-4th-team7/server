package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;
import java.util.ArrayList;
import java.util.List;

public record HabitRewardListResponse(List<HabitRewardResponse> habitRewards) {
    public static HabitRewardListResponse empty() {
        return new HabitRewardListResponse(new ArrayList<>());
    }

    public static HabitRewardListResponse from(List<Habit> habits) {
        return new HabitRewardListResponse(habits.stream().map(HabitRewardResponse::from).toList());
    }
}
