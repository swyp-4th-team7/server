package com.swyp.server.domain.user.service;

import com.swyp.server.domain.auth.repository.RefreshTokenRepository;
import com.swyp.server.domain.user.dto.InviteCodeResponse;
import com.swyp.server.domain.user.dto.UserResponse;
import com.swyp.server.domain.user.entity.Role;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.entity.UserType;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import com.swyp.server.global.util.InviteCodeGenerator;
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
    private final InviteCodeGenerator inviteCodeGenerator;

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

    @Transactional(readOnly = true)
    public UserResponse getMe(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public InviteCodeResponse getInviteCode(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.isProfileCompleted()) {
            throw new CustomException(ErrorCode.PROFILE_NOT_COMPLETED);
        }
        return InviteCodeResponse.from(user);
    }

    @Transactional
    public void updateProfile(Long userId, String nickname, UserType userType) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.completeProfile(nickname, userType, generateUniqueInviteCode());
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

    @Transactional
    public void agreeToTerms(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.agreeToTerms();
    }

    private String generateUniqueInviteCode() {
        for (int i = 0; i < 5; i++) {
            String code = inviteCodeGenerator.generate();
            if (!userRepository.existsByInviteCode(code)) {
                return code;
            }
        }
        throw new CustomException(ErrorCode.INVITE_CODE_GENERATION_FAILED);
    }
}
