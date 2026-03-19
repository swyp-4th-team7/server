package com.swyp.server.domain.habit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HabitDuration {
    THREE_DAYS("3일", 3),
    SEVEN_DAYS("7일", 7),
    FOURTEEN_DAYS("14일", 14),
    TWENTYONE_DAYS("21일", 21),
    SIXTYSIX_DAYS("66일", 66),
    NINETYNINE_DAYS("99일", 99);

    private final String label;
    private final int days;
}
