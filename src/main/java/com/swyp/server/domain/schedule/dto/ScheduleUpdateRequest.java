package com.swyp.server.domain.schedule.dto;

import com.swyp.server.domain.schedule.entity.ScheduleCategory;
import java.time.LocalDate;

public record ScheduleUpdateRequest(
        String title, ScheduleCategory category, LocalDate scheduleDate) {}
