package com.swyp.server.domain.habit.repository;

import com.swyp.server.domain.habit.entity.Habit;
import java.util.List;
import java.util.Optional;

import com.swyp.server.domain.habit.entity.RewardStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    Optional<Habit> findByIdAndUserId(Long habitId, Long userId);

    @EntityGraph(attributePaths = "user")
    List<Habit> findAllByUserIdOrderByIsCompletedAscIdDesc(Long userId);

    @EntityGraph(attributePaths = "user")
    List<Habit> findAllByUserIdAndStatusOrderByIsCompletedAscIdDesc(Long userId, RewardStatus status);

    @Query("SELECT h FROM Habit h " +
            "WHERE h.userId IN :userIds " +
            "AND (:status IS NULL OR h.status = :status) " +
            "ORDER BY h.isCompleted ASC, h.id DESC")
    List<Habit> findAllByUserIdsAndStatusOptional(
            @Param("userIds") List<Long> userIds,
            @Param("status") RewardStatus status);

}
