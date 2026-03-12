package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.TodoCategory;
import com.swyp.server.domain.todo.entity.TodoColor;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TodoUpdateRequest(
        @Size(max = 12, message = "TODO_TITLE_LENGTH_INVALID") String title,
        TodoCategory category,
        LocalDate todoDate,
        TodoColor color,
        Boolean completed) {}
