package com.swyp.server.global;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@MappedSuperclass
@SQLRestriction("deleted_at IS NULL")
public abstract class SoftDeletableEntity extends AuditableEntity {

    @Column private LocalDateTime deletedAt;

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
