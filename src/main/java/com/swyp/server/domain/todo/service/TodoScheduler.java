package com.swyp.server.domain.todo.service;

import com.swyp.server.domain.todo.repository.TodoRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodoScheduler {

    private final TodoRepository todoRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void hardDeleteTodos() {
        LocalDateTime cutoff = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(30);
        int deletedCount = todoRepository.deleteAllDeletedBefore(cutoff);

        log.info("Hard deleted {} todos", deletedCount);
    }
}
