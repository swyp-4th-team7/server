package com.swyp.server.infra.fcm;

import com.google.firebase.FirebaseApp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp(FirebaseAppInitializer initializer) {
        return initializer.initialize();
    }
}
