package com.swyp.server.global.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtils {

    public static final LocalDate APPLICATION_LAUNCH_DATE = LocalDate.of(2026, 3, 1);

    public static LocalDate getWeekStart(LocalDate today) {
        return today.with(DayOfWeek.MONDAY);
    }

    public static LocalDate getWeekEnd(LocalDate today) {
        return getWeekStart(today).plusDays(6);
    }
}
