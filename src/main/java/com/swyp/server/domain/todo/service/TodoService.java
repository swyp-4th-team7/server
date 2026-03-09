package com.swyp.server.domain.todo.service;

import com.swyp.server.domain.todo.entity.Todo;
import com.swyp.server.domain.todo.entity.TodoCategory;
import com.swyp.server.domain.todo.repository.TodoRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createTodo(Long userId, String title, TodoCategory category, LocalDate todoDate) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Todo todo =
                Todo.builder()
                        .user(user)
                        .title(title)
                        .category(category)
                        .todoDate(todoDate)
                        .build();

        return todoRepository.save(todo).getId();
    }
}
