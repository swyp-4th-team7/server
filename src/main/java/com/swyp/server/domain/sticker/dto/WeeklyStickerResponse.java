package com.swyp.server.domain.sticker.dto;

import java.time.LocalDate;
import java.util.List;

public record WeeklyStickerResponse(
        String weekLabel,
        int weekOffset,
        LocalDate startDate,
        LocalDate endDate,
        List<DateStickerResponse> stickers) {}
