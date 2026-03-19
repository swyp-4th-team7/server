package com.swyp.server.domain.habit.repository;

import com.swyp.server.domain.habit.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    Optional<Habit> findByIdAndUserId(Long habitId, Long userId);

    @Query("SELECT h FROM Habit h JOIN FETCH h.user WHERE h.user.id = :userId ORDER BY h.isCompleted ASC, h.id DESC")
    List<Habit> findAllByUserIdOrderByIsCompletedAscIdDesc(@Param("userId") Long userId);
}