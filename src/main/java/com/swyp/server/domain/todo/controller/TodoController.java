package com.swyp.server.domain.todo.controller;

import com.swyp.server.domain.todo.dto.TodoCreateRequest;
import com.swyp.server.domain.todo.dto.TodoCreateResponse;
import com.swyp.server.domain.todo.service.TodoService;
import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Todo", description = "할 일 API")
@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @Operation(summary = "할 일 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<TodoCreateResponse>> createTodo(
            @AuthenticationPrincipal Long userId, @Valid @RequestBody TodoCreateRequest request) {
        Long todoId =
                todoService.createTodo(
                        userId, request.title(), request.category(), request.todoDate());
        return ResponseEntity.ok(ApiResponse.created(new TodoCreateResponse(todoId)));
    }
}
