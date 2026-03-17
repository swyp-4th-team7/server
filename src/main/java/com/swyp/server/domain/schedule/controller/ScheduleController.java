package com.swyp.server.domain.schedule.controller;

import com.swyp.server.domain.schedule.dto.ScheduleCreateRequest;
import com.swyp.server.domain.schedule.dto.ScheduleResponse;
import com.swyp.server.domain.schedule.dto.ScheduleUpdateRequest;
import com.swyp.server.domain.schedule.entity.Schedule;
import com.swyp.server.domain.schedule.service.ScheduleService;
import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Schedule", description = "다가오는 일정 API")
@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "일정 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ScheduleCreateRequest request) {
        Schedule schedule = scheduleService.createSchedule(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(ScheduleResponse.from(schedule)));
    }

    @Operation(summary = "다가오는 일정 조회 (오늘부터 30일 이내)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedules(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.success(scheduleService.getSchedules(userId)));
    }

    @Operation(summary = "일정 수정")
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> updateSchedule(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleUpdateRequest request) {
        scheduleService.updateSchedule(userId, scheduleId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "일정 삭제")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @AuthenticationPrincipal Long userId, @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(userId, scheduleId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
