package com.swyp.server.domain.habit.controller;

import com.swyp.server.domain.habit.dto.*;
import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.RewardStatus;
import com.swyp.server.domain.habit.service.HabitService;
import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Habit", description = "습관 API")
@RestController
@RequestMapping("/api/v1/habits")
@RequiredArgsConstructor
public class HabitController {
    private final HabitService habitService;

    @Operation(summary = "습관 생성")
    @PostMapping()
    public ResponseEntity<ApiResponse<HabitCreateResponse>> createHabit(
            @AuthenticationPrincipal Long userId, @Valid @RequestBody HabitCreateRequest request) {
        Habit habit = habitService.createHabit(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(HabitCreateResponse.from(habit)));
    }

    @Operation(summary = "습관 조회")
    @GetMapping()
    public ResponseEntity<ApiResponse<HabitListResponse>> getHabits(
            @AuthenticationPrincipal Long userId) {
        HabitListResponse habits = habitService.getHabits(userId);
        return ResponseEntity.ok(ApiResponse.success(habits));
    }

    @Operation(summary = "재도전 습관 조회")
    @GetMapping("/failed")
    public ResponseEntity<ApiResponse<FailedHabitListResponse>> getFailedHabits(
            @AuthenticationPrincipal Long userId) {
        FailedHabitListResponse failedHabits = habitService.getFailedHabits(userId);
        return ResponseEntity.ok(ApiResponse.success(failedHabits));
    }

    @Operation(summary = "보상 조회")
    @GetMapping("/rewards")
    public ResponseEntity<ApiResponse<HabitRewardListResponse>> getHabitRewards(
            @RequestParam RewardStatus status, @AuthenticationPrincipal Long userId) {
        HabitRewardListResponse rewards = habitService.getHabitRewards(userId, status);
        return ResponseEntity.ok(ApiResponse.success(rewards));
    }

    @Operation(summary = "보상 상세 조회")
    @GetMapping("/{habitId}/rewards")
    public ResponseEntity<ApiResponse<HabitRewardDetailResponse>> getHabitRewardDetail(
            @AuthenticationPrincipal Long userId, @PathVariable Long habitId) {
        HabitRewardDetailResponse reward = habitService.getHabitRewardDetail(userId, habitId);
        return ResponseEntity.ok(ApiResponse.success(reward));
    }

    @Operation(summary = "습관 수정")
    @PatchMapping("/{habitId}")
    public ResponseEntity<ApiResponse<Void>> updateHabit(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long habitId,
            @Valid @RequestBody HabitUpdateRequest request) {

        habitService.updateHabit(userId, habitId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "습관 재도전")
    @PatchMapping("/failed/{habitId}/status/in-progress")
    public ResponseEntity<ApiResponse<Void>> retryFailedHabits(
            @AuthenticationPrincipal Long userId, @PathVariable Long habitId) {
        habitService.retryFailedHabit(userId, habitId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "보상 상태 수정(보상 확인중 -> 진행중)")
    @PatchMapping("/{habitId}/status/in-progress")
    public ResponseEntity<ApiResponse<Void>> updateHabitRewardStatusToInProgress(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long habitId,
            @Valid @RequestBody HabitRewardUpdateRequest request) {
        habitService.updateHabitRewardStatusToInProgress(userId, habitId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "보상 상태 수정(보상 대기중 -> 완료)")
    @PatchMapping("/{habitId}/status/complete")
    public ResponseEntity<ApiResponse<Void>> updateHabitRewardStatusToComplete(
            @AuthenticationPrincipal Long userId, @PathVariable Long habitId) {
        habitService.updateHabitRewardStatusToComplete(userId, habitId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "습관 삭제")
    @DeleteMapping("/{habitId}")
    public ResponseEntity<ApiResponse<Void>> deleteHabit(
            @AuthenticationPrincipal Long userId, @PathVariable Long habitId) {
        habitService.deleteHabit(userId, habitId);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
