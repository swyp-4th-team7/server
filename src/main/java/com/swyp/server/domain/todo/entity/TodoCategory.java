package com.swyp.server.domain.todo.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TodoCategory {
    STUDY("공부"),
    HOMEWORK("숙제"),
    EXERCISE("운동"),
    CLEANING("정리"),
    READING("독서"),
    HOUSEWORK("집안일"),
    CREATIVE_ACTIVITY("창의활동");

    private final String label;
}
