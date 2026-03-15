package com.swyp.server.global.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class DateUtils {

    public static LocalDate getWeekStart(LocalDate today) {
        return today.with(DayOfWeek.MONDAY);
    }

    public static LocalDate getWeekEnd(LocalDate today) {
        return getWeekStart(today).plusDays(6);
    }
}
