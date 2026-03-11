package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.Todo;
import com.swyp.server.domain.todo.entity.TodoCategory;
import com.swyp.server.domain.todo.entity.TodoColor;

public record TodoResponse(
        Long todoId, String title, TodoCategory category, TodoColor color, boolean completed) {
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getCategory(),
                todo.getCategory().getColor(),
                todo.isCompleted());
    }
}
