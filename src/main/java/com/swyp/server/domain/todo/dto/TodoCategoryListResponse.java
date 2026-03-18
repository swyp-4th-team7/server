package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.TodoCategory;
import java.util.List;

public record TodoCategoryListResponse(List<TodoCategoryResponse> categories) {

    public static TodoCategoryListResponse from(List<TodoCategory> categories) {
        return new TodoCategoryListResponse(
                categories.stream().map(TodoCategoryResponse::from).toList());
    }
}
