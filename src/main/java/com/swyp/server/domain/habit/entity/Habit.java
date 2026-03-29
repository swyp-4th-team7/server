package com.swyp.server.domain.habit.entity;

import com.swyp.server.domain.user.entity.User;
import com.swyp.server.global.SoftDeletableEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "habits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Habit extends SoftDeletableEntity {

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
    private HabitDuration duration;

    private String reward;

    @Column(nullable = false)
    private boolean isCompleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardStatus status;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Builder
    public Habit(User user, String title, HabitDuration duration, String reward) {
        this.user = user;
        this.title = title;
        this.duration = duration;
        this.reward = reward;
        this.isCompleted = false;
        this.status = RewardStatus.REWARD_CHECKING;
        this.expiredAt = LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(duration.getDays());
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDuration(HabitDuration duration) {
        this.duration = duration;
        this.expiredAt = this.getCreatedAt().plusDays(duration.getDays());
    }

    public void updateReward(String reward) {
        this.reward = reward;
    }

    public void complete() {
        this.isCompleted = true;
    }

    public void incomplete() {
        this.isCompleted = false;
    }

    public void updateRewardStatus(RewardStatus rewardStatus) {
        this.status = rewardStatus;
    }
}
