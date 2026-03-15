package com.swyp.server.domain.sticker.dto;

import java.time.LocalDate;
import java.util.List;

public record WeeklyStickerResponse(
        String weekLabel,
        LocalDate startDate,
        LocalDate endDate,
        List<CompletedDateStickerResponse> stickers) {}
