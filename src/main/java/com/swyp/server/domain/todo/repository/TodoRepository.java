package com.swyp.server.domain.todo.repository;

import com.swyp.server.domain.todo.entity.Todo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUserIdAndTodoDate(Long userId, LocalDate todoDate);

    Optional<Todo> findByIdAndUserId(Long todoId, Long userId);

    void deleteByUserId(Long userId);

    @Modifying
    @Query("delete from Todo t where t.deletedAt is not null and t.deletedAt <= :cutoff")
    int deleteAllDeletedBefore(@Param("cutoff") LocalDateTime cutoff);
}
