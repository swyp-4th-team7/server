package com.swyp.server.domain.habit.entity;

import com.swyp.server.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "habit_daily_completions",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_habit_daily_completion_habit_date",
                    columnNames = {"habit_id", "completion_date"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HabitDailyCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;

    @Builder
    public HabitDailyCompletion(User user, Habit habit, LocalDate completionDate) {
        this.user = user;
        this.habit = habit;
        this.completionDate = completionDate;
    }
}
