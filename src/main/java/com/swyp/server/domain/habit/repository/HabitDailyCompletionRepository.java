package com.swyp.server.domain.habit.repository;

import com.swyp.server.domain.habit.entity.HabitDailyCompletion;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HabitDailyCompletionRepository extends JpaRepository<HabitDailyCompletion, Long> {

    List<HabitDailyCompletion> findAllByUserIdAndCompletionDateBetween(
            Long userId, LocalDate startDate, LocalDate endDate);

    @Query(
            "SELECT h.user.id, COUNT(h.id) FROM HabitDailyCompletion h "
                    + "WHERE h.user.id IN :userIds "
                    + "AND h.completionDate BETWEEN :startDate AND :endDate "
                    + "GROUP BY h.user.id")
    List<Object[]> countCompletionsByUserIds(
            @Param("userIds") List<Long> userIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Modifying
    @Query(
            value = "DELETE FROM habit_daily_completions WHERE user_id = :userId",
            nativeQuery = true)
    void hardDeleteAllByUserId(@Param("userId") Long userId);

    void deleteByUserIdAndCompletionDate(Long userId, LocalDate completionDate);
}
