package com.swyp.server.infra.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTopicService {

    private final FirebaseMessaging firebaseMessaging;

    public TopicManagementResponse subscribeTopic(List<String> tokens, String topic) {
        if (tokens == null || tokens.isEmpty()) {
            return null;
        }

        try {
            return firebaseMessaging.subscribeToTopic(tokens, topic);
        } catch (FirebaseMessagingException e) {
            throw new CustomException(ErrorCode.FCM_TOPIC_SUBSCRIBE_FAILED, e);
        }
    }

    public TopicManagementResponse unsubscribeTopic(List<String> tokens, String topic) {
        if (tokens == null || tokens.isEmpty()) {
            return null;
        }

        try {
            return firebaseMessaging.unsubscribeFromTopic(tokens, topic);
        } catch (FirebaseMessagingException e) {
            throw new CustomException(ErrorCode.FCM_TOPIC_UNSUBSCRIBE_FAILED, e);
        }
    }
}
