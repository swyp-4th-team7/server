package com.swyp.server.infra.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FcmInitializer {
    private final String serviceAccountPath;

    public FcmInitializer(@Value("${firebase.service-account.path}") String serviceAccountPath) {
        this.serviceAccountPath = serviceAccountPath;
    }

    public FirebaseApp initialize() {
        List<FirebaseApp> apps = FirebaseApp.getApps();
        if (apps != null && !apps.isEmpty()) {
            log.info("FirebaseApp already initialized successfully");
            return apps.get(0);
        }

        try (FileInputStream serviceAccount = new FileInputStream(serviceAccountPath)) {

            FirebaseOptions options =
                    FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("Firebase Admin SDK initialized successfully");
            return app;

        } catch (IOException e) {
            log.error("Firebase Admin SDK initialization failed", e);
            throw new IllegalStateException("Firebase initialization failed", e);
        }
    }
}
