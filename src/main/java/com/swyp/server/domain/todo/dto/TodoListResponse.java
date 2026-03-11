package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.Todo;
import java.util.List;

public record TodoListResponse(
        int totalCount,
        int completedCount,
        int remainingCount,
        int progressPercent,
        List<TodoResponse> todos) {

    public static TodoListResponse from(List<Todo> todos) {

        int total = todos.size();
        int completed = (int) todos.stream().filter(Todo::isCompleted).count();
        int remaining = total - completed;
        int percent = 0;

        if (total != 0) {
            percent = (completed * 100) / total;
        }

        return new TodoListResponse(
                total,
                completed,
                remaining,
                percent,
                todos.stream().map(TodoResponse::from).toList());
    }
}
