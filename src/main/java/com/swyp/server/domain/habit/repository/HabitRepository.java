package com.swyp.server.domain.habit.repository;

import com.swyp.server.domain.habit.entity.Habit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    Optional<Habit> findByIdAndUserId(Long habitId, Long userId);

    @EntityGraph(attributePaths = "user")
    List<Habit> findAllByUserIdOrderByIsCompletedAscIdDesc(Long userId);
}
