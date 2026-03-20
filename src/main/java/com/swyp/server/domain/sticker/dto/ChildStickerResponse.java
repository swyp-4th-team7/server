package com.swyp.server.domain.sticker.dto;

import java.time.LocalDate;

public record ChildStickerResponse(
        Long childId,
        String nickname,
        int boardNumber,
        int filledSlots,
        int boardSize,
        LocalDate startDate) {}
