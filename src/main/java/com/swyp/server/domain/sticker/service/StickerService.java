package com.swyp.server.domain.sticker.service;

import com.swyp.server.domain.sticker.entity.UserStickerProgress;
import com.swyp.server.domain.sticker.repository.UserStickerProgressRepository;
import com.swyp.server.domain.todo.service.TodoService;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StickerService {

    private static final int BOARD_SIZE = 30;

    private final TodoService todoService;
    private final UserRepository userRepository;
    private final UserStickerProgressRepository progressRepository;

    public void confirmStickerBoard(Long userId) {
        int totalCompleted = todoService.countCompletedDates(userId);

        UserStickerProgress progress =
                progressRepository.findByUserId(userId).orElseGet(() -> createProgress(userId));

        int confirmedCount = progress.getLastConfirmedCompletedDateCount();
        int currentCompleted = totalCompleted - confirmedCount;
        int confirmTarget = confirmedCount + (currentCompleted / BOARD_SIZE) * BOARD_SIZE;

        progress.confirmBoard(confirmTarget);
    }

    private UserStickerProgress createProgress(Long userId) {
        User user = userRepository.getReferenceById(userId);

        return progressRepository.save(
                UserStickerProgress.builder()
                        .user(user)
                        .lastConfirmedCompletedDateCount(0)
                        .build());
    }
}
