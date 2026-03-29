package com.swyp.server.domain.habit.service;

import com.swyp.server.domain.family.service.FamilyRelationService;
import com.swyp.server.domain.habit.dto.*;
import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.HabitDailyCompletion;
import com.swyp.server.domain.habit.entity.RewardStatus;
import com.swyp.server.domain.habit.repository.HabitDailyCompletionRepository;
import com.swyp.server.domain.habit.repository.HabitRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.entity.UserType;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import com.swyp.server.global.notification.NotificationService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class HabitService {
    private final FamilyRelationService familyRelationService;
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final HabitDailyCompletionRepository habitDailyCompletionRepository;

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

        Habit savedHabit = habitRepository.save(habit);

        if (user.getUserType() == UserType.CHILD) {
            List<Long> parentIds =
                    familyRelationService.getConnectedMembers(userId).stream()
                            .filter(m -> m.getUserType() == UserType.PARENT)
                            .map(User::getId)
                            .toList();
            if (!parentIds.isEmpty()) {
                TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronization() {
                            @Override
                            public void afterCommit() {
                                notificationService.sendToUsers(
                                        parentIds, "해봄", "새로운 보상이 추가됐어요! 지금 바로 수락해 볼까요?", Map.of());
                            }
                        });
            }
        }

        return savedHabit;
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

        RewardStatus filteredStatus = status.isAll() ? null : status;

        List<Habit> habits =
                habitRepository.findAllByUserIdsAndStatusOptional(targetUserIds, filteredStatus);

        return HabitRewardListResponse.from(habits, user.getUserType());
    }

    @Transactional(readOnly = true)
    public HabitRewardDetailResponse getHabitRewardDetail(Long userId, Long habitId) {
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

        if (habit.getStatus() == RewardStatus.IN_PROGRESS
                || habit.getStatus() == RewardStatus.COMPLETE) {
            throw new CustomException(ErrorCode.GET_REWARD_DETAIL_FORBIDDEN);
        }

        List<User> connectedMembers = familyRelationService.getConnectedMembers(user.getId());

        boolean isYourChild =
                connectedMembers.stream()
                        .map(User::getId)
                        .toList()
                        .contains(habit.getUser().getId());

        if (!isYourChild) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

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
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
            // 오늘 이미 기록된 경우에 중복 저장 방지
            if (!habitDailyCompletionRepository.existsByUserIdAndCompletionDate(userId, today)) {
                habitDailyCompletionRepository.save(
                        HabitDailyCompletion.builder()
                                .user(habit.getUser())
                                .completionDate(today)
                                .build());
            }
        } else {
            habit.incomplete();
        }
    }

    @Transactional
    public void updateHabitRewardStatusToInProgress(
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

        List<User> connectedMembers = familyRelationService.getConnectedMembers(user.getId());

        boolean isYourChild =
                connectedMembers.stream()
                        .map(User::getId)
                        .toList()
                        .contains(habit.getUser().getId());

        if (!isYourChild) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (habit.getReward() != null
                && !habit.getReward().trim().equals(request.reward().trim())) {
            habit.updateReward(request.reward());
        }

        RewardStatus previousStatus = habit.getStatus();

        if (previousStatus != RewardStatus.REWARD_CHECKING) {
            throw new CustomException(ErrorCode.INVALID_HABIT_STATUS);
        }

        habit.updateRewardStatus(RewardStatus.IN_PROGRESS);

        Long childId = habit.getUser().getId();
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        notificationService.sendToUser(
                                childId, "해봄", "부모님이 보상을 허락해 주셨어요. 보상을 받을 때까지 파이팅!", Map.of());
                    }
                });
    }

    @Transactional
    public void updateHabitRewardStatusToComplete(Long userId, Long habitId) {
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

        List<User> connectedMembers = familyRelationService.getConnectedMembers(user.getId());

        boolean isYourChild =
                connectedMembers.stream()
                        .map(User::getId)
                        .toList()
                        .contains(habit.getUser().getId());

        if (!isYourChild) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (habit.getStatus() != RewardStatus.REWARD_WAITING) {
            throw new CustomException(ErrorCode.INVALID_HABIT_STATUS);
        }

        habit.updateRewardStatus(RewardStatus.COMPLETE);
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
