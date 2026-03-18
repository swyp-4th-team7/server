package com.swyp.server.domain.habit.service;

import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.HabitDuration;
import com.swyp.server.domain.habit.repository.HabitRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HabitService {
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    @Transactional
    public Habit createChildHabit(Long userId, String title, HabitDuration duration, String reward){
        User user = userRepository.
                findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Habit habit =
                Habit.builder()
                        .user(user)
                        .title(title)
                        .duration(duration)
                        .reward(reward)
                        .build();

        return habitRepository.save(habit);
    }

    @Transactional
    public Habit createParentHabit(Long userId, String title, HabitDuration duration){
        User user = userRepository.
                findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Habit habit =
                Habit.builder()
                        .user(user)
                        .title(title)
                        .duration(duration)
                        .build();

        return habitRepository.save(habit);
    }

    @Transactional
    public void updateChildHabit(Long userId, Long habitId, String title, HabitDuration duration, String reward, Boolean isCompleted) {
        Habit habit = habitRepository
                .findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.HABIT_NOT_FOUND));

        habit.updateTitle(title);
        habit.updateDuration(duration);
        habit.updateReward(reward);

        if(isCompleted){
            habit.complete();
        } else{
            habit.incomplete();
        }

    }

    @Transactional
    public void updateParentHabit(Long userId, Long habitId, String title, HabitDuration duration, Boolean isCompleted) {
        Habit habit = habitRepository
                .findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.HABIT_NOT_FOUND));

        habit.updateTitle(title);
        habit.updateDuration(duration);

        if(isCompleted){
            habit.complete();
        }else{
            habit.incomplete();
        }
    }

    @Transactional
    public void deleteHabit(Long userId, Long habitId) {
        Habit habit = habitRepository.
                findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.HABIT_NOT_FOUND));

        habit.delete();
    }

}
