package com.swyp.server.domain.sticker.entity;

import com.swyp.server.global.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stickers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sticker extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean basic;

    @Column(length = 500)
    private String imageUrl;

    @Builder
    public Sticker(String code, String name, boolean basic, String imageUrl) {
        this.code = code;
        this.name = name;
        this.basic = basic;
        this.imageUrl = imageUrl;
    }
}
