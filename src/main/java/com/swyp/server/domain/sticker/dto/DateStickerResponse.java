package com.swyp.server.domain.sticker.dto;

import java.time.LocalDate;

public record DateStickerResponse(LocalDate date, String stickerCode) {}
