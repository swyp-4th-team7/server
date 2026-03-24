package com.swyp.server.domain.habit.service;

import com.swyp.server.domain.habit.repository.HabitRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class HabitScheduler {

    private final HabitRepository habitRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void checkExpiredHabits() {
        log.info("만료된 습관 체크 스케줄러 시작");
        habitRepository.updateExpiredHabitsStatus(LocalDateTime.now());
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 0시 0분 0초
    public void resetDailyHabits() {
        log.info("매일 습관 완료 상태 초기화 스케줄러 시작");
        habitRepository.resetAllHabits();
    }
}
