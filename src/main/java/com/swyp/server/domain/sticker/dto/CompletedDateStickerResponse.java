package com.swyp.server.domain.sticker.dto;

import java.time.LocalDate;

public record CompletedDateStickerResponse(LocalDate date, String stickerCode) {}
