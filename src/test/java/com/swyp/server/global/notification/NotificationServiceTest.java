package com.swyp.server.global.notification;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.TopicManagementResponse;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import com.swyp.server.infra.fcm.service.FcmSendService;
import com.swyp.server.infra.fcm.service.FcmTokenService;
import com.swyp.server.infra.fcm.service.FcmTopicService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock FcmTokenService fcmTokenService;

    @Mock FcmSendService fcmSendService;

    @Mock FcmTopicService fcmTopicService;

    @InjectMocks NotificationService notificationService;

    @DisplayName("토큰이 없으면 전송을 시도하지 않는다.")
    @Test
    void whenNoTokens_doNotSend() {
        Long userId = 1L;
        when(fcmTokenService.findTokenStringsByUserId(userId)).thenReturn(List.of());

        notificationService.sendToUser(userId, "title", "body", Map.of());

        verifyNoInteractions(fcmSendService);
        verify(fcmTokenService, never()).deleteByToken(anyString());
    }

    @DisplayName("전송 실패 코드가 무효 토큰 코드이면 토큰을 삭제한다.")
    @ParameterizedTest
    @EnumSource(
            value = MessagingErrorCode.class,
            names = {"UNREGISTERED", "INVALID_ARGUMENT"})
    void whenSendFailsWithInvalidTokenErrorCode_deleteToken(MessagingErrorCode errorCode) {
        Long userId = 1L;
        String token = "test-token";

        FirebaseMessagingException fme = mock(FirebaseMessagingException.class);

        when(fcmTokenService.findTokenStringsByUserId(userId)).thenReturn(List.of(token));
        when(fme.getMessagingErrorCode()).thenReturn(errorCode);

        doThrow(new CustomException(ErrorCode.FCM_SEND_FAILED, fme))
                .when(fcmSendService)
                .sendToToken(eq(token), anyString(), anyString(), anyMap());

        notificationService.sendToUser(userId, "title", "body", Map.of());

        verify(fcmTokenService).deleteByToken(token);
    }

    @DisplayName("전송 실패 코드가 유효 토큰 코드라면 토큰을 삭제하지 않는다.")
    @ParameterizedTest
    @EnumSource(
            value = MessagingErrorCode.class,
            mode = Mode.EXCLUDE,
            names = {"UNREGISTERED", "INVALID_ARGUMENT"})
    void whenSendFailsWithValidTokenErrorCode_doNotDeleteToken(MessagingErrorCode errorCode) {

        Long userId = 1L;
        String token = "test-token";

        when(fcmTokenService.findTokenStringsByUserId(userId)).thenReturn(List.of(token));

        FirebaseMessagingException fme = mock(FirebaseMessagingException.class);

        when(fme.getMessagingErrorCode()).thenReturn(errorCode);

        doThrow(new CustomException(ErrorCode.FCM_SEND_FAILED, fme))
                .when(fcmSendService)
                .sendToToken(eq(token), anyString(), anyString(), anyMap());

        notificationService.sendToUser(userId, "title", "body", Map.of());

        verify(fcmTokenService, never()).deleteByToken(anyString());
    }

    @DisplayName("전송 실패 원인이 FirebaseMessagingException이 아니면 토큰을 삭제하지 않는다.")
    @Test
    void whenSendFailsWithNonFirebaseCause_doNotDeleteToken() {
        Long userId = 1L;
        String token = "test-token";

        when(fcmTokenService.findTokenStringsByUserId(userId)).thenReturn(List.of(token));

        doThrow(
                        new CustomException(
                                ErrorCode.FCM_SEND_FAILED,
                                new RuntimeException("not-firebase-messaging-exception")))
                .when(fcmSendService)
                .sendToToken(eq(token), anyString(), anyString(), anyMap());

        notificationService.sendToUser(userId, "title", "body", Map.of());

        verify(fcmTokenService, never()).deleteByToken(anyString());
    }

    @DisplayName("토픽 구독 결과가 부분 실패여도 실패 토큰을 삭제하지 않는다.")
    @Test
    void whenSubscribeFails_doNotDeleteFailedToken() {
        Long userId = 1L;
        String topic = "test-topic";
        List<String> tokens = List.of("test-token-1", "test-token-2", "test-token-3");

        when(fcmTokenService.findTokenStringsByUserId(userId)).thenReturn(tokens);

        TopicManagementResponse response = mock(TopicManagementResponse.class);
        when(response.getSuccessCount()).thenReturn(2);
        when(response.getFailureCount()).thenReturn(1);

        TopicManagementResponse.Error error = mock(TopicManagementResponse.Error.class);
        when(error.getIndex()).thenReturn(0);
        when(error.getReason()).thenReturn("ANY_REASON");
        when(response.getErrors()).thenReturn(List.of(error));

        when(fcmTopicService.subscribeTopic(tokens, topic)).thenReturn(response);

        notificationService.subscribeTopic(userId, topic);

        verify(fcmTokenService, never()).deleteByToken(anyString());
    }

    @DisplayName("토픽 구독 중 예외가 발생하면 토큰을 삭제하지 않는다.")
    @Test
    void whenSubscribeTopicThrowsException_doNotDeleteToken() {
        Long userId = 1L;
        String topic = "test-topic";
        List<String> tokens = List.of("test-token-1", "test-token-2");

        when(fcmTokenService.findTokenStringsByUserId(userId)).thenReturn(tokens);

        when(fcmTopicService.subscribeTopic(tokens, topic))
                .thenThrow(new CustomException(ErrorCode.FCM_TOPIC_SUBSCRIBE_FAILED));

        notificationService.subscribeTopic(userId, topic);
        verify(fcmTokenService, never()).deleteByToken(anyString());
    }
}
