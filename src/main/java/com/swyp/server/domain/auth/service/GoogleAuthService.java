package com.swyp.server.domain.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.swyp.server.domain.auth.dto.GoogleLoginRequest;
import com.swyp.server.domain.auth.dto.LoginResponse;
import com.swyp.server.domain.auth.entity.RefreshToken;
import com.swyp.server.domain.auth.repository.RefreshTokenRepository;
import com.swyp.server.domain.user.entity.Role;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.config.JwtProvider;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Transactional
    public LoginResponse googleLogin(GoogleLoginRequest request) {
        GoogleIdToken.Payload payload = verifyGoogleToken(request.idToken());

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        boolean isNewUser = !userRepository.existsByEmail(email);

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseGet(
                                () ->
                                        userRepository.save(
                                                User.builder()
                                                        .email(email)
                                                        .nickname(name)
                                                        .profileImageUrl(pictureUrl)
                                                        .role(Role.USER)
                                                        .build()));

        String accessToken = jwtProvider.generateAccessToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        refreshTokenRepository
                .findByUserId(user.getId())
                .ifPresentOrElse(
                        existing -> existing.updateToken(refreshToken),
                        () ->
                                refreshTokenRepository.save(
                                        RefreshToken.builder()
                                                .userId(user.getId())
                                                .token(refreshToken)
                                                .build()));

        return LoginResponse.of(accessToken, refreshToken, isNewUser);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public LoginResponse reissue(String refreshToken) {
        String token = refreshToken.replace("Bearer ", "");

        // 만료 포함 모든 검증을 직접 처리
        Long userId;
        try {
            userId = jwtProvider.getUserIdFromToken(token);
        } catch (CustomException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // DB에서 토큰 일치 여부 확인
        refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        refreshTokenRepository.findByUserId(userId).ifPresent(t -> t.updateToken(newRefreshToken));

        return LoginResponse.of(newAccessToken, newRefreshToken, false);
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                            .setAudience(Collections.singletonList(googleClientId))
                            .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }
            return googleIdToken.getPayload();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Google token verification failed: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}
