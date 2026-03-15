package com.swyp.server.domain.sticker.repository;

import com.swyp.server.domain.sticker.entity.Sticker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StickerRepository extends JpaRepository<Sticker, Long> {

    Optional<Sticker> findByCode(String code);

    List<Sticker> findAllByBasicTrue();
}
