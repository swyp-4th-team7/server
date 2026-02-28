package com.swyp.server.global.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.TopicManagementResponse;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.infra.fcm.service.FcmSendService;
import com.swyp.server.infra.fcm.service.FcmTokenService;
import com.swyp.server.infra.fcm.service.FcmTopicService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FcmTokenService fcmTokenService;
    private final FcmSendService fcmSendService;
    private final FcmTopicService fcmTopicService;

    public void subscribeTopic(Long userId, String topic) {
        List<String> tokens = fcmTokenService.findTokenStringsByUserId(userId);

        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        TopicManagementResponse response;
        try {
            response = fcmTopicService.subscribeTopic(tokens, topic);
        } catch (CustomException e) {
            log.warn("Topic subscribe failed userId={}, topic={}", userId, topic, e);
            return;
        }

        cleanUpFcmTokens(tokens, response, "subscribe", topic);
    }

    public void unsubscribeTopic(Long userId, String topic) {
        List<String> tokens = fcmTokenService.findTokenStringsByUserId(userId);

        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        TopicManagementResponse response;
        try {
            response = fcmTopicService.unsubscribeTopic(tokens, topic);
        } catch (CustomException e) {
            log.warn("Topic unsubscribe failed userId={}, topic={}", userId, topic, e);
            return;
        }

        cleanUpFcmTokens(tokens, response, "unsubscribe", topic);
    }

    public void sendToUsers(
            List<Long> userIds, String title, String body, Map<String, String> data) {
        if (userIds == null || userIds.isEmpty()) return;

        userIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .forEach(id -> sendToUser(id, title, body, data));
    }

    public void sendToUser(Long userId, String title, String body, Map<String, String> data) {
        List<String> tokens = fcmTokenService.findTokenStringsByUserId(userId);
        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        for (String token : tokens) {
            try {
                fcmSendService.sendToToken(token, title, body, data);
            } catch (CustomException e) {
                handleSendFailure(userId, token, e);
            }
        }
    }

    public void sendToTopic(String topic, String title, String body, Map<String, String> data) {
        try {
            fcmSendService.sendToTopic(topic, title, body, data);
        } catch (CustomException e) {
            log.warn("FCM sendToTopic failed topic={}", topic, e);
        }
    }

    // topic subscribe/unsubscribe 실패 토큰 삭제
    private void cleanUpFcmTokens(
            List<String> tokens, TopicManagementResponse response, String operation, String topic) {
        if (response == null || response.getFailureCount() <= 0) {
            return;
        }

        log.warn(
                "FCM topic {} partial failed topic={}, success={}, failure={}",
                operation,
                topic,
                response.getSuccessCount(),
                response.getFailureCount());

        for (TopicManagementResponse.Error error : response.getErrors()) {
            int idx = error.getIndex();
            if (idx < 0 || idx >= tokens.size()) {
                continue;
            }

            String failedToken = tokens.get(idx);
            fcmTokenService.deleteByToken(failedToken);

            log.info(
                    "Deleted failed token operation={}, topic={}, reason={}, token={}",
                    operation,
                    topic,
                    error.getReason(),
                    maskToken(failedToken));
        }
    }

    private void handleSendFailure(Long userId, String token, CustomException e) {
        if (isInvalidFcmToken(e)) {
            fcmTokenService.deleteByToken(token);
            log.info("Deleted invalid FCM token userId={}, token={}", userId, maskToken(token));
            return;
        }
        log.error("FCM sendToUser failed userId={}, token={}", userId, maskToken(token), e);
    }

    private boolean isInvalidFcmToken(CustomException e) {
        Throwable cause = e.getCause();
        if (!(cause instanceof FirebaseMessagingException fme)) {
            return false;
        }

        MessagingErrorCode code = fme.getMessagingErrorCode();
        return code == MessagingErrorCode.UNREGISTERED
                || code == MessagingErrorCode.INVALID_ARGUMENT;
    }

    private String maskToken(String token) {
        if (token == null) {
            return "null";
        }
        return token.substring(0, Math.min(8, token.length())) + "...(len=" + token.length() + ")";
    }
}
