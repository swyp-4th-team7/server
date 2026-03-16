package com.swyp.server.domain.sticker.repository;

import com.swyp.server.domain.sticker.entity.UserStickerProgress;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStickerProgressRepository extends JpaRepository<UserStickerProgress, Long> {

    Optional<UserStickerProgress> findByUserId(Long userId);
}
