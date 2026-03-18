package com.swyp.server.domain.schedule.repository;

import com.swyp.server.domain.schedule.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByUserIdAndScheduleDateBetweenOrderByScheduleDateAsc(
            Long userId, LocalDate start, LocalDate end);

    Optional<Schedule> findByIdAndUserId(Long scheduleId, Long userId);

    @Modifying
    @Query(value = "DELETE FROM schedules WHERE user_id = :userId", nativeQuery = true)
    void hardDeleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(
            value = "DELETE FROM schedules WHERE deleted_at IS NOT NULL AND deleted_at <= :cutoff",
            nativeQuery = true)
    int deleteAllDeletedBefore(@Param("cutoff") LocalDateTime cutoff);
}
