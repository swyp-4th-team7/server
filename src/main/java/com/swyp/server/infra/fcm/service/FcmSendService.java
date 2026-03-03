package com.swyp.server.infra.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmSendService {

    private final FirebaseMessaging firebaseMessaging;

    public String sendToToken(String token, String title, String body, Map<String, String> data) {
        Message message =
                Message.builder()
                        .setToken(token)
                        .setNotification(
                                Notification.builder().setTitle(title).setBody(body).build())
                        .putAllData(resolveData(data))
                        .build();

        return sendMessage(message);
    }

    public String sendToTopic(String topic, String title, String body, Map<String, String> data) {
        Message message =
                Message.builder()
                        .setTopic(topic)
                        .setNotification(
                                Notification.builder().setTitle(title).setBody(body).build())
                        .putAllData(resolveData(data))
                        .build();

        return sendMessage(message);
    }

    private String sendMessage(Message message) {
        try {
            return firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new CustomException(ErrorCode.FCM_SEND_FAILED, e);
        }
    }

    private Map<String, String> resolveData(Map<String, String> data) {
        if (data == null) {
            return Map.of();
        }
        return Map.copyOf(data);
    }
}
