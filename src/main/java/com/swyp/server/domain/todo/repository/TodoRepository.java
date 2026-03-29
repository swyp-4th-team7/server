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

    List<Todo> findAllByUserIdAndTodoDateBetween(
            Long userId, LocalDate startDate, LocalDate endDate);

    List<Todo> findAllByUserIdInAndTodoDate(List<Long> userIds, LocalDate todoDate);

    @Modifying
    @Query(value = "DELETE FROM todos WHERE user_id = :userId", nativeQuery = true)
    void hardDeleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(
            value = "DELETE FROM todos WHERE deleted_at IS NOT NULL AND deleted_at <= :cutoff",
            nativeQuery = true)
    int deleteAllDeletedBefore(@Param("cutoff") LocalDateTime cutoff);

    @Query(
            "SELECT DISTINCT t.user.id FROM Todo t WHERE t.user.id IN :userIds AND t.todoDate = :date AND t.completed = false AND t.deletedAt IS NULL")
    List<Long> findUserIdsWithIncompleteTodo(
            @Param("userIds") List<Long> userIds, @Param("date") LocalDate date);

    @Query(
            "SELECT COUNT(t) FROM Todo t WHERE t.user.id = :userId "
                    + "AND t.todoDate BETWEEN :startDate AND :endDate "
                    + "AND t.deletedAt IS NULL")
    int countAllByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(
            "SELECT COUNT(t) FROM Todo t WHERE t.user.id = :userId "
                    + "AND t.todoDate BETWEEN :startDate AND :endDate "
                    + "AND t.completed = true "
                    + "AND t.deletedAt IS NULL")
    int countCompletedByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
