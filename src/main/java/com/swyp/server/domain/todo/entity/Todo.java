package com.swyp.server.domain.todo.entity;

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
@Table(name = "todos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Todo extends SoftDeletableEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoColor color;

    @Column(nullable = false)
    private LocalDate todoDate;

    @Column(nullable = false)
    private boolean completed;

    @Builder
    public Todo(
            User user, String title, TodoCategory category, TodoColor color, LocalDate todoDate) {
        this.user = user;
        this.title = title;
        this.category = category;
        this.color = color;
        this.todoDate = todoDate;
        this.completed = false;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateCategory(TodoCategory category) {
        this.category = category;
    }

    public void updateColor(TodoColor color) {
        this.color = color;
    }

    public void updateTodoDate(LocalDate todoDate) {
        this.todoDate = todoDate;
    }

    public void complete() {
        this.completed = true;
    }

    public void incomplete() {
        this.completed = false;
    }
}
