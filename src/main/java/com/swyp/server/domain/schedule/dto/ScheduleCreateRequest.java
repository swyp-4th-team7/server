package com.swyp.server.domain.schedule.dto;

import com.swyp.server.domain.schedule.entity.ScheduleCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ScheduleCreateRequest(
        @NotBlank(message = "SCHEDULE_TITLE_REQUIRED") String title,
        @NotNull(message = "SCHEDULE_CATEGORY_REQUIRED") ScheduleCategory category,
        @NotNull(message = "SCHEDULE_DATE_REQUIRED") LocalDate scheduleDate) {}
