package com.swyp.server.domain.sticker.service;

import com.swyp.server.domain.sticker.dto.CompletedDateStickerResponse;
import com.swyp.server.domain.sticker.dto.WeeklyStickerResponse;
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

    // 초기 개발 단계에서는 DEFAULT 스티커 하나로 고정하였음
    private static final String DEFAULT_STICKER_CODE = "BASIC_STICKER";

    private final TodoService todoService;

    public WeeklyStickerResponse getWeeklyStickers(Long userId, LocalDate today) {
        LocalDate startDate = DateUtils.getWeekStart(today);
        LocalDate endDate = DateUtils.getWeekEnd(today);
        List<LocalDate> completedDates = todoService.getCompletedDates(userId, startDate, endDate);

        List<CompletedDateStickerResponse> stickers =
                completedDates.stream()
                        .map(date -> new CompletedDateStickerResponse(date, DEFAULT_STICKER_CODE))
                        .toList();

        return new WeeklyStickerResponse(createWeekLabel(today), startDate, endDate, stickers);
    }

    private String createWeekLabel(LocalDate today) {
        int month = today.getMonthValue();
        int weekOfMonth = (today.getDayOfMonth() / -1) / 7 + 1;
        return month + "월 " + weekOfMonth + "주차";
    }
}
