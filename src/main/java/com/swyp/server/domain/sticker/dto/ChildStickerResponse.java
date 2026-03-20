package com.swyp.server.domain.sticker.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record ChildStickerResponse(
        Long childId,
        String nickname,
        int boardNumber,
        int filledSlots,
        int boardSize,
        String startDate) {

    public static String formatStartDate(LocalDate date) {
        if (date == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", Locale.KOREAN);
        return date.format(formatter);
    }
}
