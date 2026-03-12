package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.Todo;
import com.swyp.server.domain.todo.entity.TodoCategory;
import com.swyp.server.domain.todo.entity.TodoColor;
import java.time.LocalDate;

public record TodoCreateResponse(
        Long todoId,
        String title,
        TodoCategory category,
        LocalDate todoDate,
        TodoColor color,
        boolean completed) {

    public static TodoCreateResponse from(Todo todo) {
        return new TodoCreateResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getCategory(),
                todo.getTodoDate(),
                todo.getColor(),
                todo.isCompleted());
    }
}
