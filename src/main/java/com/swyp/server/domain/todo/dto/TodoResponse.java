package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.Todo;
import com.swyp.server.domain.todo.entity.TodoCategory;

public record TodoResponse(Long todoId, String title, TodoCategory category, boolean completed) {
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(), todo.getTitle(), todo.getCategory(), todo.isCompleted());
    }
}
