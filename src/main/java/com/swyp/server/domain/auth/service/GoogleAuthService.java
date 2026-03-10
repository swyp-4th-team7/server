package com.swyp.server.domain.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.swyp.server.domain.auth.dto.LoginResponse;
import com.swyp.server.domain.auth.dto.SocialLoginRequest;
import com.swyp.server.domain.auth.entity.RefreshToken;
import com.swyp.server.domain.auth.repository.RefreshTokenRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.service.UserService;
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

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Transactional
    public LoginResponse login(SocialLoginRequest request) {
        if (!"GOOGLE".equals(request.socialType())) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        GoogleIdToken.Payload payload = verifyGoogleToken(request.token());

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        User user = userService.findOrCreateUser(email, name, pictureUrl);

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

        return new LoginResponse(
                accessToken, refreshToken, user.getUserType(), user.isProfileCompleted());
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
            jwtProvider.validateToken(token);
            userId = jwtProvider.getUserIdFromToken(token);
        } catch (CustomException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // DB에서 토큰 일치 여부 확인
        refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        User user = userService.findById(userId);

        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        refreshTokenRepository.findByUserId(userId).ifPresent(t -> t.updateToken(newRefreshToken));

        return new LoginResponse(
                newAccessToken, newRefreshToken, user.getUserType(), user.isProfileCompleted());
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
