package com.swyp.server.domain.notification;

import com.swyp.server.domain.todo.repository.TodoRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.entity.UserType;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.notification.NotificationService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushNotificationScheduler {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final NotificationService notificationService;

    // 매일 09시 - 하루 시작 알림 (모든 유저)
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void sendDayStartNotification() {
        List<User> users = userRepository.findAllActive();
        List<Long> userIds = users.stream().map(User::getId).toList();
        notificationService.sendToUsers(userIds, "해봄", "하루가 시작되었습니다. 할 일을 추가해 볼까요?", Map.of());
        log.info("Day start notification sent to {} users", userIds.size());
    }

    // 매일 17시 - 미완료 할 일 리마인드 (자녀)
    @Scheduled(cron = "0 0 17 * * *", zone = "Asia/Seoul")
    public void sendTodoIncompleteNotification() {
        LocalDate today = LocalDate.now(SEOUL_ZONE);
        List<User> children = userRepository.findAllActiveByUserType(UserType.CHILD);
        List<Long> childIds = children.stream().map(User::getId).toList();

        List<Long> targetIds = todoRepository.findUserIdsWithIncompleteTodo(childIds, today);

        notificationService.sendToUsers(
                targetIds, "해봄", "할 일을 아직 하지 않았어요. 지금 바로 완료해 보세요!", Map.of());
        log.info("Todo incomplete notification sent to {} children", targetIds.size());
    }
}
