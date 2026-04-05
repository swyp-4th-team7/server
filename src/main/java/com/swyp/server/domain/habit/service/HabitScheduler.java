package com.swyp.server.domain.habit.service;

import com.swyp.server.domain.family.service.FamilyRelationService;
import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.repository.HabitRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.entity.UserType;
import com.swyp.server.global.notification.NotificationService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
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
    private final NotificationService notificationService;
    private final FamilyRelationService familyRelationService;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkExpiredHabits() {
        log.info("만료된 습관 체크 스케줄러 시작");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        List<Habit> expiredHabits = habitRepository.findExpiredHabits(now);

        expiredHabits.stream()
                .filter(h -> h.getUser().getUserType() == UserType.CHILD)
                .forEach(
                        h -> {
                            notificationService.sendToUser(
                                    h.getUser().getId(),
                                    "해봄",
                                    "보상을 받을 수 있어요. 지금 바로 확인해 볼까요?",
                                    Map.of());

                            List<Long> parentIds =
                                    familyRelationService
                                            .getConnectedMembers(h.getUser().getId())
                                            .stream()
                                            .filter(m -> m.getUserType() == UserType.PARENT)
                                            .map(User::getId)
                                            .toList();
                            if (!parentIds.isEmpty()) {
                                notificationService.sendToUsers(
                                        parentIds, "해봄", "자녀가 습관을 완료했어요. 보상을 줄 시간이에요!", Map.of());
                            }
                        });

        habitRepository.updateExpiredHabitsStatus(now);
    }

    @Transactional
    @Scheduled(cron = "0 1 0 * * *", zone = "Asia/Seoul")
    public void resetDailyHabits() {
        log.info("매일 습관 완료 상태 초기화 스케줄러 시작");
        habitRepository.resetAllHabits();
    }
}
