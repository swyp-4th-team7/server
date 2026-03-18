package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.TodoCategory;

public record TodoCategoryResponse(String name, String label) {

    public static TodoCategoryResponse from(TodoCategory todoCategory) {
        return new TodoCategoryResponse(todoCategory.name(), todoCategory.getLabel());
    }
}
