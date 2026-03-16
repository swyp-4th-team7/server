package com.swyp.server.domain.sticker.entity;

import com.swyp.server.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_sticker_progress")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStickerProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private int lastConfirmedCompletedDateCount;

    @Builder
    public UserStickerProgress(User user, int lastConfirmedCompletedDateCount) {
        this.user = user;
        this.lastConfirmedCompletedDateCount = lastConfirmedCompletedDateCount;
    }

    public void confirmBoard(int completedDateCount) {
        this.lastConfirmedCompletedDateCount = completedDateCount;
    }
}
