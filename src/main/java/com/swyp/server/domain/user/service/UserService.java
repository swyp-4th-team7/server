package com.swyp.server.domain.user.service;

import com.swyp.server.domain.auth.repository.RefreshTokenRepository;
import com.swyp.server.domain.user.entity.Role;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.entity.UserType;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import com.swyp.server.infra.fcm.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public User findOrCreateUser(String email, String nickname, String profileImageUrl) {
        return userRepository
                .findByEmail(email)
                .orElseGet(
                        () ->
                                userRepository.save(
                                        User.builder()
                                                .email(email)
                                                .nickname(nickname)
                                                .profileImageUrl(profileImageUrl)
                                                .role(Role.USER)
                                                .build()));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void updateProfile(Long userId, String nickname, UserType userType) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.completeProfile(nickname, userType);
    }

    @Transactional
    public void withdraw(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteByUserId(userId);
        fcmTokenRepository.deleteByUserId(userId);
        user.delete();
    }
}
