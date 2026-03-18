package com.swyp.server.domain.habit.repository;

import com.swyp.server.domain.habit.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    Optional<Habit> findByIdAndUserId(Long todoId, Long userId);
    List<Habit> findAllByUserIdOrderByIsCompletedAscIdDesc(Long userId);
}
