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
    List<Todo> findAllByUserIdAndTodoDateOrderByCompletedAscCreatedAtAsc(
            Long userId, LocalDate todoDate);

    Optional<Todo> findByIdAndUserId(Long todoId, Long userId);

    @Modifying
    @Query(value = "DELETE FROM todos WHERE user_id = :userId", nativeQuery = true)
    void hardDeleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(
            value = "DELETE FROM todos WHERE deleted_at IS NOT NULL AND deleted_at <= :cutoff",
            nativeQuery = true)
    int deleteAllDeletedBefore(@Param("cutoff") LocalDateTime cutoff);
}
