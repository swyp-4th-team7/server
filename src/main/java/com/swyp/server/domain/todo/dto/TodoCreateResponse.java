package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.Todo;
import com.swyp.server.domain.todo.entity.TodoCategory;
import com.swyp.server.domain.todo.entity.TodoColor;

public record TodoCreateResponse(
        Long todoId, String title, TodoCategory category, TodoColor color, boolean completed) {

    public static TodoCreateResponse from(Todo todo) {
        return new TodoCreateResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getCategory(),
                todo.getColor(),
                todo.isCompleted());
    }
}
