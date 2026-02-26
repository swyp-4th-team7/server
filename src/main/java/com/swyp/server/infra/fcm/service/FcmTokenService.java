package com.swyp.server.infra.fcm.service;

import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import com.swyp.server.infra.fcm.entity.FcmToken;
import com.swyp.server.infra.fcm.entity.Platform;
import com.swyp.server.infra.fcm.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    // FCM token은 디바이스 식별자이므로
    // 동일 token 재등록시 중복 저장하지 않고 갱신한다.
    @Transactional
    public void registerFcmToken(Long userId, String token, Platform platform) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        fcmTokenRepository
                .findByToken(token)
                .ifPresentOrElse(
                        existingToken -> existingToken.update(user, platform),
                        () ->
                                fcmTokenRepository.save(
                                        FcmToken.builder()
                                                .user(user)
                                                .platform(platform)
                                                .token(token)
                                                .build()));
    }

    @Transactional
    public void delete(Long userId, String token) {
        fcmTokenRepository.deleteByUserIdAndToken(userId, token);
    }
}
