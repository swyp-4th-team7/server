package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.TodoCategory;
import com.swyp.server.domain.todo.entity.TodoColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TodoCreateRequest(
        @NotBlank(message = "TODO_TITLE_REQUIRED") String title,
        @NotNull(message = "TODO_CATEGORY_REQUIRED") TodoCategory category,
        @NotNull(message = "TODO_COLOR_REQUIRED") TodoColor color) {}
