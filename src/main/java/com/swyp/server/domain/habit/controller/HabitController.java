package com.swyp.server.domain.habit.controller;

import com.swyp.server.domain.habit.dto.*;
import com.swyp.server.domain.habit.entity.Habit;
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

import java.util.List;

@Tag(name = "Habit", description = "습관 API")
@RestController
@RequestMapping("/api/v1/habits")
@RequiredArgsConstructor
public class HabitController {
    private final HabitService habitService;

    @Operation(summary = "습관 생성")
    @PostMapping()
    public ResponseEntity<ApiResponse<HabitCreateResponse>> createHabit(
            @AuthenticationPrincipal Long userId, @Valid @RequestBody HabitCreateRequest request){
        Habit habit = habitService.createHabit(
                userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(HabitCreateResponse.from(habit)));
    }

    @Operation(summary = "습관 조회")
    @GetMapping()
    public ResponseEntity<ApiResponse<HabitListResponse>> getHabits(@AuthenticationPrincipal Long userId){
        List<Habit> habits = habitService.getHabits(userId);
        return ResponseEntity.ok(ApiResponse.success(HabitListResponse.from(habits)));
    }

    @Operation(summary = "습관 수정")
    @PatchMapping("/{habitId}")
    public ResponseEntity<ApiResponse<Void>> updateHabit(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long habitId,
            @Valid @RequestBody HabitUpdateRequest request){

        habitService.updateHabit(
                userId,
                habitId,
                request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "습관 삭제")
    @DeleteMapping("/{habitId}")
    public ResponseEntity<ApiResponse<Void>> deleteHabit(@AuthenticationPrincipal Long userId, @PathVariable Long habitId){
        habitService.deleteHabit(userId, habitId);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
