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

    Optional<Habit> findByIdAndUserIdAndStatus(Long habitId, Long userId, RewardStatus status);

    @EntityGraph(attributePaths = "user")
    @Query(
            "SELECT h FROM Habit h "
                    + "WHERE h.user.id = :userId "
                    + "AND h.status IN ('REWARD_CHECKING', 'IN_PROGRESS') "
                    + "ORDER BY h.isCompleted ASC, h.id DESC")
    List<Habit> findAllActiveHabitsByUserId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = "user")
    @Query("SELECT h FROM Habit h " + "WHERE h.user.id = :userId " + "AND h.status = 'FAIL'")
    List<Habit> findAllFailedHabitsByUserId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = "user")
    List<Habit> findAllByUserIdAndStatusOrderByIsCompletedAscIdDesc(
            Long userId, RewardStatus status);

    @EntityGraph(attributePaths = "user")
    @Query(
            "SELECT h FROM Habit h "
                    + "WHERE h.user.id IN :userIds "
                    + "AND h.status IN :statuses "
                    + "ORDER BY "
                    + "  CASE h.status WHEN 'COMPLETE' THEN 1 ELSE 0 END ASC, "
                    + "  h.id DESC")
    List<Habit> findAllByUserIdsAndStatusOptional(
            @Param("userIds") List<Long> userIds, @Param("statuses") List<RewardStatus> statuses);

    @EntityGraph(attributePaths = "user")
    @Query("SELECT h FROM Habit h WHERE h.status = 'IN_PROGRESS' AND h.expiredAt < :now")
    List<Habit> findExpiredHabits(@Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE Habit h "
                    + "SET h.status = CASE "
                    + "    WHEN h.user.userType = 'PARENT' THEN 'COMPLETE' "
                    + // 부모면 바로 완료
                    "    ELSE 'REWARD_WAITING' "
                    + // 아니면 보상 대기
                    "END "
                    + "WHERE h.status = 'IN_PROGRESS' "
                    + "AND h.expiredAt < :now")
    void updateExpiredHabitsStatus(@Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE Habit h SET h.status = 'FAIL' "
                    + " WHERE h.isCompleted = false "
                    + " AND h.status IN ('REWARD_CHECKING', 'IN_PROGRESS')"
                    + " AND h.duration IN ('THREE_DAYS', 'SEVEN_DAYS')")
    void updateImmediateFailureHabits();

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE Habit h SET h.status = 'FAIL', h.isCompleted = false "
                    + " WHERE h.failCount > 1 "
                    + " AND h.status IN ('REWARD_CHECKING', 'IN_PROGRESS')"
                    + " AND h.duration NOT IN ('THREE_DAYS', 'SEVEN_DAYS')")
    void updateCumulativeFailureHabits();

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE Habit h SET h.failCount = h.failCount + 1 "
                    + " WHERE h.isCompleted = false "
                    + " AND h.status IN ('REWARD_CHECKING', 'IN_PROGRESS') "
                    + " AND h.duration NOT IN ('THREE_DAYS', 'SEVEN_DAYS')")
    void updateHabitFailCount();

    @Modifying(clearAutomatically = true)
    @Query(
            value =
                    "UPDATE habits h SET h.fail_count = 0 "
                            + "WHERE DATEDIFF(:today, h.created_at) > 0 "
                            + "AND DATEDIFF(:today, h.created_at) % 10 = 0 "
                            + "AND h.fail_count < 2 "
                            + "AND h.status IN ('REWARD_CHECKING', 'IN_PROGRESS') "
                            + "AND h.duration NOT IN ('THREE_DAYS', 'SEVEN_DAYS')",
            nativeQuery = true)
    void resetFailCount(@Param("today") LocalDateTime today);

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE Habit h SET h.isCompleted = false "
                    + " WHERE h.isCompleted = true "
                    + " AND h.status IN ('REWARD_CHECKING', 'IN_PROGRESS')")
    void resetAllHabits();

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
