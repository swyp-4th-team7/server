package com.swyp.server.domain.habit.dto;

import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.user.entity.UserType;

public record HabitRewardResponse(
        Long habitId,
        String title,
        String nickname,
        String duration,
        String reward,
        boolean isCompleted,
        String status) {
    public static HabitRewardResponse from(Habit habit, UserType viewerType) {

        String reward =
                (habit.getUser().getUserType() == (UserType.PARENT)) ? null : habit.getReward();

        String nickname =
                (viewerType == (UserType.PARENT))
                        ? habit.getUser().getNickname()
                        : null;

        return new HabitRewardResponse(
                habit.getId(),
                habit.getTitle(),
                nickname,
                habit.getDuration().getLabel(),
                reward,
                habit.isCompleted(),
                habit.getStatus().getLabel());
    }
}
