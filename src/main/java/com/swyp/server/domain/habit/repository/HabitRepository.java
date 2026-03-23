package com.swyp.server.domain.habit.repository;

import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.RewardStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    Optional<Habit> findByIdAndUserId(Long habitId, Long userId);

    @EntityGraph(attributePaths = "user")
    List<Habit> findAllByUserIdOrderByIsCompletedAscIdDesc(Long userId);

    @EntityGraph(attributePaths = "user")
    List<Habit> findAllByUserIdAndStatusOrderByIsCompletedAscIdDesc(
            Long userId, RewardStatus status);

    @EntityGraph(attributePaths = "user")
    @Query(
            "SELECT h FROM Habit h "
                    + "WHERE h.user.id IN :userIds "
                    + "AND (:status IS NULL OR h.status = :status) "
                    + "ORDER BY h.isCompleted ASC, h.id DESC")
    List<Habit> findAllByUserIdsAndStatusOptional(
            @Param("userIds") List<Long> userIds, @Param("status") RewardStatus status);

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE Habit h SET h.status = 'REWARD_WAITING' "
                    + "WHERE h.status = 'IN_PROGRESS' "
                    + "AND h.expiredAt < :now")
    void updateExpiredHabitsStatus(@Param("now") LocalDateTime now);

    // 미완료 습관이 있는 유저 ID 조회
    @Query(
            "SELECT DISTINCT h.user.id FROM Habit h WHERE h.user.id IN :userIds AND h.isCompleted = false")
    List<Long> findUserIdsWithIncompleteHabit(@Param("userIds") List<Long> userIds);

    // 습관이 하나도 없는 유저 ID 조회
    @Query(
            "SELECT u.id FROM User u WHERE u.id IN :userIds AND u.id NOT IN (SELECT DISTINCT h.user.id FROM Habit h WHERE h.user.id IN :userIds)")
    List<Long> findUserIdsWithNoHabit(@Param("userIds") List<Long> userIds);

    @Modifying
    @Query(value = "DELETE FROM habits WHERE user_id = :userId", nativeQuery = true)
    void hardDeleteAllByUserId(@Param("userId") Long userId);
}
