package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.HabitDuration;
import com.swyp.server.domain.habit.entity.RewardStatus;
import com.swyp.server.domain.user.entity.UserType;

public record HabitRewardResponse(
        Long habitId,
        String title,
        String nickname,
        HabitDuration duration,
        String reward,
        RewardStatus status) {
    public static HabitRewardResponse from(Habit habit, UserType viewerType) {

        String nickname = (viewerType == (UserType.PARENT)) ? habit.getUser().getNickname() : null;

        return new HabitRewardResponse(
                habit.getId(),
                habit.getTitle(),
                nickname,
                habit.getDuration(),
                habit.getReward(),
                habit.getStatus());
    }
}
