package com.swyp.server.domain.user.service;

import com.swyp.server.domain.habit.repository.HabitDailyCompletionRepository;
import com.swyp.server.domain.habit.repository.HabitRepository;
import com.swyp.server.domain.schedule.repository.ScheduleRepository;
import com.swyp.server.domain.sticker.repository.UserStickerRepository;
import com.swyp.server.domain.todo.repository.TodoRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.infra.fcm.repository.FcmTokenRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserWithdrawalScheduler {

    private final UserRepository userRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final TodoRepository todoRepository;
    private final UserStickerRepository userStickerRepository;
    private final ScheduleRepository scheduleRepository;
    private final HabitRepository habitRepository;
    private final HabitDailyCompletionRepository habitDailyCompletionRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void hardDeleteWithdrawnUsers() {
        LocalDateTime cutoff = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(30);
        List<User> deletedUsers = userRepository.findAllDeletedBefore(cutoff);

        if (deletedUsers.isEmpty()) {
            return;
        }

        deletedUsers.forEach(user -> fcmTokenRepository.deleteByUserId(user.getId()));
        deletedUsers.forEach(user -> todoRepository.hardDeleteAllByUserId(user.getId()));
        deletedUsers.forEach(user -> userStickerRepository.deleteAllByUserId(user.getId()));
        deletedUsers.forEach(user -> scheduleRepository.hardDeleteAllByUserId(user.getId()));
        deletedUsers.forEach(
                user -> habitDailyCompletionRepository.hardDeleteAllByUserId(user.getId()));
        deletedUsers.forEach(user -> habitRepository.hardDeleteAllByUserId(user.getId()));

        userRepository.deleteAll(deletedUsers);
        log.info("Hard deleted {} withdrawn users", deletedUsers.size());
    }
}
