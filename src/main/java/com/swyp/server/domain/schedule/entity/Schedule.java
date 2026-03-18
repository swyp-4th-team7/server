package com.swyp.server.domain.schedule.entity;

import com.swyp.server.domain.user.entity.User;
import com.swyp.server.global.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "schedules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Schedule extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleCategory category;

    @Column(nullable = false)
    private LocalDate scheduleDate;

    @Builder
    public Schedule(User user, String title, ScheduleCategory category, LocalDate scheduleDate) {
        this.user = user;
        this.title = title;
        this.category = category;
        this.scheduleDate = scheduleDate;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateCategory(ScheduleCategory category) {
        this.category = category;
    }

    public void updateScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
}
