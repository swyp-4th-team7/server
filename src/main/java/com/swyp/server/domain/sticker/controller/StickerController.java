package com.swyp.server.domain.sticker.controller;

import com.swyp.server.domain.sticker.dto.WeeklyStickerResponse;
import com.swyp.server.domain.sticker.service.StickerQueryService;
import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sticker", description = "스티커 API")
@RestController
@RequestMapping("/api/v1/stickers")
@RequiredArgsConstructor
public class StickerController {

    private final StickerQueryService stickerQueryService;

    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<WeeklyStickerResponse>> getWeeklyStickers(
            @AuthenticationPrincipal Long userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        WeeklyStickerResponse response = stickerQueryService.getWeeklyStickers(userId, today);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
