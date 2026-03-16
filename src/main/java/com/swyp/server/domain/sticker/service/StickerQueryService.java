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

    public WeeklyStickerResponse getWeeklyStickers(Long userId, LocalDate today) {
        LocalDate startDate = DateUtils.getWeekStart(today);
        LocalDate endDate = DateUtils.getWeekEnd(today);
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

        return new WeeklyStickerResponse(createWeekLabel(today), startDate, endDate, stickers);
    }

    public StickerBoardResponse getStickerBoard(Long userId) {
        int totalCompleted = todoService.countCompletedDates(userId);

        int confirmedCount =
                progressRepository
                        .findByUserId(userId)
                        .map(UserStickerProgress::getLastConfirmedCompletedDateCount)
                        .orElse(0);

        int progressCount = Math.max(totalCompleted - confirmedCount, 0);

        int filledSlots = calculateFilledSlots(progressCount);
        boolean showPopup = shouldShowPopup(progressCount);

        return new StickerBoardResponse(BOARD_SIZE, filledSlots, showPopup);
    }

    private int calculateFilledSlots(int progressCount) {
        if (progressCount == 0) {
            return 0;
        }

        int remain = progressCount % BOARD_SIZE;
        return remain == 0 ? BOARD_SIZE : remain;
    }

    private boolean shouldShowPopup(int progressCount) {
        return progressCount > 0 && progressCount % BOARD_SIZE == 0;
    }

    private String createWeekLabel(LocalDate today) {
        int month = today.getMonthValue();
        int weekOfMonth = (today.getDayOfMonth() - 1) / 7 + 1;
        return month + "월 " + weekOfMonth + "주차";
    }
}
