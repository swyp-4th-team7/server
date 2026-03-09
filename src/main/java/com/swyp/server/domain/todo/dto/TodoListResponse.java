package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.Todo;
import java.util.List;

public record TodoListResponse(List<TodoResponse> todos) {

    public static TodoListResponse from(List<Todo> todos) {
        return new TodoListResponse(todos.stream().map(TodoResponse::from).toList());
    }
}
