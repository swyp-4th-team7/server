package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.TodoCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TodoCreateRequest(
        @NotBlank(message = "TODO_TITLE_REQUIRED")
                @Size(max = 50, message = "TODO_TITLE_LENGTH_INVALID")
                String title,
        @NotNull(message = "TODO_CATEGORY_CATEGORY_REQUIRED") TodoCategory category,
        @NotNull(message = "TODO_DATE_REQUIRED") LocalDate todoDate) {}
