package com.swyp.server.domain.user.service;

import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
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

    // 매일 자정 실행
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void hardDeleteWithdrawnUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        List<User> deletedUsers = userRepository.findAllDeletedBefore(cutoff);

        if (deletedUsers.isEmpty()) {
            return;
        }

        userRepository.deleteAll(deletedUsers);
        log.info("Hard deleted {} withdrawn users", deletedUsers.size());
    }
}
