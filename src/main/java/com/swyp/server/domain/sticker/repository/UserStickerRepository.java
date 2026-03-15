package com.swyp.server.domain.sticker.repository;

import com.swyp.server.domain.sticker.entity.UserSticker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStickerRepository extends JpaRepository<UserSticker, Long> {
    List<UserSticker> findAllByUserId(Long userId);

    boolean existsByUserIdAndStickerId(Long userId, Long stickerId);
}
