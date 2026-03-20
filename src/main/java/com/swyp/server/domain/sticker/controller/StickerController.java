package com.swyp.server.domain.sticker.controller;

import com.swyp.server.domain.sticker.dto.ChildrenStickerResponse;
import com.swyp.server.domain.sticker.dto.StickerBoardResponse;
import com.swyp.server.domain.sticker.dto.WeeklyStickerResponse;
import com.swyp.server.domain.sticker.service.StickerQueryService;
import com.swyp.server.domain.sticker.service.StickerService;
import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sticker", description = "스티커 API")
@RestController
@RequestMapping("/api/v1/stickers")
@RequiredArgsConstructor
public class StickerController {

    private final StickerQueryService stickerQueryService;
    private final StickerService stickerService;

    @Operation(summary = "주간 스티커 조회")
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<WeeklyStickerResponse>> getWeeklyStickers(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int weekOffset) {

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        WeeklyStickerResponse response =
                stickerQueryService.getWeeklyStickers(userId, today, weekOffset);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "스티커판 조회")
    @GetMapping("/board")
    public ResponseEntity<ApiResponse<StickerBoardResponse>> getStickerBoard(
            @AuthenticationPrincipal Long userId) {
        StickerBoardResponse response = stickerQueryService.getStickerBoard(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "스티커판 완료 팝업 확인")
    @PostMapping("/board/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmStickerBoard(
            @AuthenticationPrincipal Long userId) {
        stickerService.confirmStickerBoard(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "부모용 자녀 스티커 현황 조회")
    @GetMapping("/children")
    public ResponseEntity<ApiResponse<ChildrenStickerResponse>> getChildrenStickers(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(stickerQueryService.getChildrenStickers(userId)));
    }
}
