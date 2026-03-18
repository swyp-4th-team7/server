package com.swyp.server.domain.schedule.dto;

import com.swyp.server.domain.schedule.entity.Schedule;
import com.swyp.server.domain.schedule.entity.ScheduleCategory;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public record ScheduleResponse(
        Long scheduleId,
        String title,
        ScheduleCategory category,
        LocalDate scheduleDate,
        long dDay) {

    public static ScheduleResponse from(Schedule schedule) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        long dDay = ChronoUnit.DAYS.between(today, schedule.getScheduleDate());
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getCategory(),
                schedule.getScheduleDate(),
                dDay);
    }
}
