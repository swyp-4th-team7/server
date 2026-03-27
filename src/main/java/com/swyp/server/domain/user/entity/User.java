package com.swyp.server.domain.user.entity;

import com.swyp.server.global.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column private String nickname;

    @Column private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column
    private UserType userType;

    @Column(unique = true, length = 8)
    private String inviteCode;

    @Column(nullable = false)
    private boolean profileCompleted;

    @Column(nullable = false)
    private boolean termsAgreed;

    @Column private LocalDateTime termsAgreedAt;

    @Builder
    public User(String email, String nickname, String profileImageUrl, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.profileCompleted = false;
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public void completeProfile(String nickname, UserType userType, String inviteCode) {
        this.nickname = nickname;
        this.userType = userType;
        this.inviteCode = inviteCode;
        this.profileCompleted = true;
    }

    public void agreeToTerms() {
        this.termsAgreed = true;
        this.termsAgreedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public void maskEmail() {
        this.email = this.email + "_" + System.currentTimeMillis();
    }
}
