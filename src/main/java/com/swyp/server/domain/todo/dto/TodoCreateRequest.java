package com.swyp.server.domain.todo.dto;

import com.swyp.server.domain.todo.entity.TodoCategory;
import java.time.LocalDate;

public record TodoCreateRequest(
        // todo validation 추가하기
        String title, TodoCategory category, LocalDate todoDate) {}
