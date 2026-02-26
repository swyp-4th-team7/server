package com.swyp.server.infra.fcm;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByToken(String token);

    List<FcmToken> findAllByUserId(Long userId);

    void deleteByUserIdAndToken(Long userId, String token);
}
