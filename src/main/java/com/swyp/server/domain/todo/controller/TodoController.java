package com.swyp.server.domain.todo.controller;

import com.swyp.server.domain.todo.dto.TodoCategoryListResponse;
import com.swyp.server.domain.todo.dto.TodoCreateRequest;
import com.swyp.server.domain.todo.dto.TodoCreateResponse;
import com.swyp.server.domain.todo.dto.TodoListResponse;
import com.swyp.server.domain.todo.dto.TodoUpdateRequest;
import com.swyp.server.domain.todo.entity.Todo;
import com.swyp.server.domain.todo.entity.TodoCategory;
import com.swyp.server.domain.todo.service.TodoService;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.service.UserService;
import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final UserService userService;

    @Operation(summary = "할 일 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<TodoCreateResponse>> createTodo(
            @AuthenticationPrincipal Long userId, @Valid @RequestBody TodoCreateRequest request) {
        Todo todo =
                todoService.createTodo(
                        userId, request.title(), request.category(), request.color());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(TodoCreateResponse.from(todo)));
    }

    @Operation(summary = "오늘의 할 일 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<TodoListResponse>> getTodayTodos(
            @AuthenticationPrincipal Long userId) {

        List<Todo> todos = todoService.getTodayTodos(userId);
        return ResponseEntity.ok(ApiResponse.success(TodoListResponse.from(todos)));
    }

    @Operation(summary = "할 일 수정")
    @PatchMapping("/{todoId}")
    public ResponseEntity<ApiResponse<Void>> updateTodo(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long todoId,
            @Valid @RequestBody TodoUpdateRequest request) {

        todoService.updateTodo(
                userId,
                todoId,
                request.title(),
                request.category(),
                request.color(),
                request.completed());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "할 일 삭제")
    @DeleteMapping("/{todoId}")
    public ResponseEntity<ApiResponse<Void>> deleteTodo(
            @AuthenticationPrincipal Long userId, @PathVariable Long todoId) {
        todoService.deleteTodo(userId, todoId);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "할 일 카테고리 조회")
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<?>> getCategories(@AuthenticationPrincipal Long userId) {

        User user = userService.findById(userId);
        List<TodoCategory> categories = todoService.getCategories(user.getUserType());

        return ResponseEntity.ok(ApiResponse.success(TodoCategoryListResponse.from(categories)));
    }
}
