package com.swyp.server.domain.habit.service;

import com.swyp.server.domain.habit.repository.HabitRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkExpiredHabits() {
        log.info("만료된 습관 체크 스케줄러 시작");
        habitRepository.updateExpiredHabitsStatus(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
    }

    @Transactional
    @Scheduled(cron = "0 1 0 * * *", zone = "Asia/Seoul")
    public void resetDailyHabits() {
        log.info("매일 습관 완료 상태 초기화 스케줄러 시작");
        habitRepository.resetAllHabits();
    }
}
