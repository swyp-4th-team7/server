package com.swyp.server.domain.sticker.entity;

import com.swyp.server.domain.user.entity.User;
import com.swyp.server.global.AuditableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_stickers",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_user_sticker_user_id_sticker_id",
                    columnNames = {"user_id", "sticker_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSticker extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sticker_id", nullable = false)
    private Sticker sticker;

    @Builder
    public UserSticker(User user, Sticker sticker) {
        this.user = user;
        this.sticker = sticker;
    }
}
