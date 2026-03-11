package com.swyp.server.infra.fcm.repository;

import com.swyp.server.infra.fcm.entity.FcmToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByToken(String token);

    List<FcmToken> findAllByUserId(Long userId);

    void deleteByUserIdAndToken(Long userId, String token);

    void deleteByToken(String token);

    void deleteByUserId(Long userId);
}
