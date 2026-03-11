package com.swyp.server.domain.todo.entity;

import static com.swyp.server.domain.todo.entity.TodoColor.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TodoCategory {
    STUDY(RED),
    HOMEWORK(PINK),
    READING(PURPLE),
    CREATIVITY(BLUE),
    HEALTH(SKYBLUE),
    ORGANIZATION(MINT),
    FOCUS(EMERALD),
    HOUSEHOLD(LIME);

    private final TodoColor color;
}
