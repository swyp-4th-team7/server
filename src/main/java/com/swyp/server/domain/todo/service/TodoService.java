package com.swyp.server.domain.todo.service;

import com.swyp.server.domain.todo.entity.Todo;
import com.swyp.server.domain.todo.entity.TodoCategory;
import com.swyp.server.domain.todo.entity.TodoColor;
import com.swyp.server.domain.todo.repository.TodoRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public Todo createTodo(Long userId, String title, TodoCategory category, TodoColor color) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Todo todo =
                Todo.builder()
                        .user(user)
                        .title(title)
                        .category(category)
                        .color(color)
                        .todoDate(today)
                        .build();

        return todoRepository.save(todo);
    }

    @Transactional
    public void updateTodo(
            Long userId,
            Long todoId,
            String title,
            TodoCategory category,
            TodoColor color,
            Boolean completed) {
        Todo todo =
                todoRepository
                        .findByIdAndUserId(todoId, userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        if (title != null) {
            if (title.isBlank()) {
                throw new CustomException(ErrorCode.TODO_TITLE_REQUIRED);
            }
            todo.updateTitle(title);
        }

        if (category != null) {
            todo.updateCategory(category);
        }

        if (color != null) {
            todo.updateColor(color);
        }

        if (completed != null) {
            if (completed) {
                todo.complete();
            } else {
                todo.incomplete();
            }
        }
    }

    @Transactional
    public void deleteTodo(Long userId, Long todoId) {
        Todo todo =
                todoRepository
                        .findByIdAndUserId(todoId, userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        todo.delete();
    }

    @Transactional(readOnly = true)
    public List<Todo> getTodayTodos(Long userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        return todoRepository.findAllByUserIdAndTodoDateOrderByCompletedAscCreatedAtAsc(
                userId, today);
    }
}
