package com.swyp.server.domain.todo.repository;

import com.swyp.server.domain.todo.entity.Todo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUserIdAndTodoDate(Long userId, LocalDate todoDate);

    Optional<Todo> findByIdAndUserId(Long todoId, Long userId);
}
