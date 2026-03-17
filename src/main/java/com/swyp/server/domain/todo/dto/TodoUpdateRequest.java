package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.TodoCategory;
import com.swyp.server.domain.todo.entity.TodoColor;
import java.time.LocalDate;

public record TodoUpdateRequest(
        String title,
        TodoCategory category,
        LocalDate todoDate,
        TodoColor color,
        Boolean completed) {}
