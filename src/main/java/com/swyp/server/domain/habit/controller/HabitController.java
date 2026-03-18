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

@Tag(name = "Habit", description = "습관 API")
@RestController
@RequestMapping("/api/v1/habits")
@RequiredArgsConstructor
public class HabitController {
    private final HabitService habitService;

    @Operation(summary = "습관 생성(자녀)")
    @PostMapping
    public ResponseEntity<ApiResponse<ChildHabitCreateResponse>> createChildHabit(
            @AuthenticationPrincipal Long userId, @Valid @RequestBody ChildHabitCreateRequest request){
        Habit habit = habitService.createChildHabit(
                userId, request.title(), request.duration(), request.reward());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(ChildHabitCreateResponse.from(habit)));
    }

    @Operation(summary = "습관 생성(부모)")
    @PostMapping
    public ResponseEntity<ApiResponse<ParentHabitCreateResponse>> createParentHabit(
            @AuthenticationPrincipal Long userId, @Valid @RequestBody ParentHabitCreateRequest request){
        Habit habit = habitService.createParentHabit(
                userId, request.title(), request.duration());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(ParentHabitCreateResponse.from(habit)));
    }

    @Operation(summary = "습관 수정(자녀)")
    @PatchMapping("/{habitId}")
    public ResponseEntity<ApiResponse<Void>> updateChildHabit(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long habitId,
            @Valid @RequestBody ChildHabitUpdateRequest request){

        habitService.updateChildHabit(
                userId,
                habitId,
                request.title(),
                request.duration(),
                request.reward(),
                request.isCompleted());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "습관 수정(부모)")
    @PatchMapping("/{habitId}")
    public ResponseEntity<ApiResponse<Void>> updateParentHabit(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long habitId,
            @Valid @RequestBody ParentHabitUpdateRequest request){

        habitService.updateParentHabit(
                userId,
                habitId,
                request.title(),
                request.duration(),
                request.isCompleted());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "습관 삭제")
    @DeleteMapping("/{habitId}")
    public ResponseEntity<ApiResponse<Void>> deleteHabit(@AuthenticationPrincipal Long userId, @PathVariable Long habitId){
        habitService.deleteHabit(userId, habitId);

        return ResponseEntity.ok(ApiResponse.success(null));
    }






}
