package com.swyp.server.domain.sticker.service;

import com.swyp.server.domain.sticker.dto.DateStickerResponse;
import com.swyp.server.domain.sticker.dto.StickerBoardResponse;
import com.swyp.server.domain.sticker.dto.WeeklyStickerResponse;
import com.swyp.server.domain.sticker.entity.UserStickerProgress;
import com.swyp.server.domain.sticker.repository.UserStickerProgressRepository;
import com.swyp.server.domain.todo.service.TodoService;
import com.swyp.server.global.util.DateUtils;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StickerQueryService {

    // 초기 개발 단계에서는 DEFAULT 스티커 하나, 보드판의 크기는 30으로 고정하였음
    private static final String DEFAULT_STICKER_CODE = "BASIC_STICKER";
    private static final int BOARD_SIZE = 30;

    private final TodoService todoService;
    private final UserStickerProgressRepository progressRepository;

    public WeeklyStickerResponse getWeeklyStickers(Long userId, LocalDate today, int weekOffset) {
        LocalDate targetDate = today.plusWeeks(weekOffset);

        LocalDate startDate = DateUtils.getWeekStart(targetDate);
        LocalDate endDate = DateUtils.getWeekEnd(targetDate);
        List<LocalDate> completedDates = todoService.getCompletedDates(userId, startDate, endDate);

        List<DateStickerResponse> stickers =
                startDate
                        .datesUntil(endDate.plusDays(1))
                        .map(
                                date -> {
                                    String stickerCode = null;

                                    if (completedDates.contains(date)) {
                                        stickerCode = DEFAULT_STICKER_CODE;
                                    }
                                    return new DateStickerResponse(date, stickerCode);
                                })
                        .toList();

        return new WeeklyStickerResponse(
                createWeekLabel(targetDate), weekOffset, startDate, endDate, stickers);
    }

    public StickerBoardResponse getStickerBoard(Long userId) {
        int totalCompleted = todoService.countCompletedDates(userId);

        int confirmedCount =
                progressRepository
                        .findByUserId(userId)
                        .map(UserStickerProgress::getLastConfirmedCompletedDateCount)
                        .orElse(0);

        int filledSlots = calculateFilledSlots(totalCompleted);

        int currentBoard = totalCompleted / BOARD_SIZE;
        int confirmedBoard = confirmedCount / BOARD_SIZE;
        boolean showPopup = shouldShowPopup(filledSlots, currentBoard, confirmedBoard);

        return new StickerBoardResponse(BOARD_SIZE, filledSlots, showPopup);
    }

    private int calculateFilledSlots(int totalCompleted) {
        if (totalCompleted == 0) {
            return 0;
        }

        int remain = totalCompleted % BOARD_SIZE;
        return remain == 0 ? BOARD_SIZE : remain;
    }

    private boolean shouldShowPopup(int filledSlots, int currentBoard, int confirmedBoard) {

        return filledSlots == BOARD_SIZE && currentBoard > confirmedBoard;
    }

    private String createWeekLabel(LocalDate targetDate) {
        int month = targetDate.getMonthValue();
        int weekOfMonth = (targetDate.getDayOfMonth() - 1) / 7 + 1;
        return month + "월 " + weekOfMonth + "주차";
    }
}
