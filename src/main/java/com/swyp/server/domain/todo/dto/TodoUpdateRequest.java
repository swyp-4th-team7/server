package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.TodoCategory;
import java.time.LocalDate;

public record TodoUpdateRequest(
        // todo validation 추가
        String title, TodoCategory category, LocalDate todoDate, Boolean completed) {}
