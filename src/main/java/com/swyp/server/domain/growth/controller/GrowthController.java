package com.swyp.server.domain.growth.controller;

import com.swyp.server.domain.growth.dto.ChildrenGrowthResponse;
import com.swyp.server.domain.growth.dto.GrowthHabitResponse;
import com.swyp.server.domain.growth.dto.GrowthTodoResponse;
import com.swyp.server.domain.growth.service.GrowthService;
import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Growth", description = "성장탭 API")
@RestController
@RequestMapping("/api/v1/growth")
@RequiredArgsConstructor
public class GrowthController {

    private final GrowthService growthService;

    @Operation(summary = "자녀 - 이번 주 할 일 별 조회")
    @GetMapping("/todo")
    public ResponseEntity<ApiResponse<GrowthTodoResponse>> getTodoGrowth(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.success(growthService.getTodoGrowth(userId)));
    }

    @Operation(summary = "자녀 - 이번 주 습관 별 조회")
    @GetMapping("/habit")
    public ResponseEntity<ApiResponse<GrowthHabitResponse>> getHabitGrowth(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.success(growthService.getHabitGrowth(userId)));
    }

    @Operation(summary = "부모 - 연결된 자녀 성장 현황 조회")
    @GetMapping("/children")
    public ResponseEntity<ApiResponse<ChildrenGrowthResponse>> getChildrenGrowth(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.success(growthService.getChildrenGrowth(userId)));
    }
}
