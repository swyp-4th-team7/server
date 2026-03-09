package com.swyp.server.domain.todo.entity;

import com.swyp.server.domain.user.entity.User;
import com.swyp.server.global.AuditableEntity;
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

@Entity
@Table(name = "todos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoCategory category;

    @Column(nullable = false)
    private LocalDate todoDate;

    @Column(nullable = false)
    private boolean completed;

    @Builder
    public Todo(User user, String title, TodoCategory category, LocalDate todoDate) {
        this.user = user;
        this.title = title;
        this.category = category;
        this.todoDate = todoDate;
        this.completed = false;
    }

    public void update(String title, TodoCategory category, LocalDate todoDate) {
        this.title = title;
        this.category = category;
        this.todoDate = todoDate;
    }

    public void complete() {
        this.completed = true;
    }

    public void incomplete() {
        this.completed = false;
    }
}
