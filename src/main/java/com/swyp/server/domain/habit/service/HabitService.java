package com.swyp.server.domain.habit.service;

import com.swyp.server.domain.family.service.FamilyRelationService;
import com.swyp.server.domain.habit.dto.*;
import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.RewardStatus;
import com.swyp.server.domain.habit.repository.HabitRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.entity.UserType;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HabitService {
    private final FamilyRelationService familyRelationService;
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    @Transactional
    public Habit createHabit(Long userId, HabitCreateRequest request) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String reward = null;

        if (user.getUserType() == UserType.CHILD) {
            if (request.reward() == null || request.reward().isBlank())
                throw new CustomException(ErrorCode.HABIT_REWARD_REQUIRED);
            reward = request.reward();
        }

        Habit habit =
                Habit.builder()
                        .user(user)
                        .title(request.title())
                        .duration(request.duration())
                        .reward(reward)
                        .build();

        return habitRepository.save(habit);
    }

    @Transactional(readOnly = true)
    public HabitListResponse getHabits(Long userId) {
        List<Habit> habits = habitRepository.findAllByUserIdOrderByIsCompletedAscIdDesc(userId);
        return HabitListResponse.from(habits);
    }

    @Transactional(readOnly = true)
    public HabitRewardListResponse getHabitRewards(Long userId, RewardStatus status) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Long> targetUserIds = new ArrayList<>();

        if (user.getUserType() == UserType.CHILD) {
            targetUserIds.add(userId);
        } else {
            List<User> connectedMembers = familyRelationService.getConnectedMembers(userId);
            List<Long> childIds =
                    connectedMembers.stream()
                            .filter(m -> m.getUserType() == UserType.CHILD)
                            .map(User::getId)
                            .toList();

            targetUserIds.addAll(childIds);
        }

        if (targetUserIds.isEmpty()) return HabitRewardListResponse.empty();

        List<Habit> habits =
                habitRepository.findAllByUserIdsAndStatusOptional(targetUserIds, status);

        return HabitRewardListResponse.from(habits);
    }

    @Transactional(readOnly = true)
    public HabitRewardDetailResponse getHabitRewardDetail(Long userId, Long habitId) {
        Habit habit =
                habitRepository
                        .findByIdAndUserId(habitId, userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.HABIT_NOT_FOUND));
        return HabitRewardDetailResponse.from(habit);
    }

    @Transactional
    public void updateHabit(Long userId, Long habitId, HabitUpdateRequest request) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String reward = null;

        if (user.getUserType() == UserType.CHILD) {
            if (request.reward() == null || request.reward().isBlank())
                throw new CustomException(ErrorCode.HABIT_REWARD_REQUIRED);
            reward = request.reward();
        }

        Habit habit =
                habitRepository
                        .findByIdAndUserId(habitId, userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.HABIT_NOT_FOUND));

        habit.updateTitle(request.title());
        habit.updateDuration(request.duration());
        habit.updateReward(reward);

        if (request.isCompleted()) {
            habit.complete();
        } else {
            habit.incomplete();
        }
    }

    @Transactional
    public void updateHabitRewardStatus(
            Long userId, Long habitId, HabitRewardUpdateRequest request) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getUserType() == UserType.CHILD) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        Habit habit =
                habitRepository
                        .findById(habitId)
                        .orElseThrow(() -> new CustomException(ErrorCode.HABIT_NOT_FOUND));

        if (request.rewardStatus() == RewardStatus.COMPLETE) {
            habit.updateRewardStatus(request.rewardStatus());
            habit.complete();
        } else {
            habit.updateRewardStatus(request.rewardStatus());
        }
    }

    @Transactional
    public void deleteHabit(Long userId, Long habitId) {
        Habit habit =
                habitRepository
                        .findByIdAndUserId(habitId, userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.HABIT_NOT_FOUND));

        habit.delete();
    }
}
