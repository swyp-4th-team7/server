package com.swyp.server.infra.fcm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.swyp.server.infra.fcm.config.FcmInitializer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

class FcmInitializerTest {
    @DisplayName("키 파일이 없으면 IllegalStateException을 던진다.")
    @Test
    void whenKeyFileMissing_throwIllegalStateException() {
        FcmInitializer initializer = new FcmInitializer("/no/such/file/key.json");

        try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class)) {
            firebaseAppMock.when(FirebaseApp::getApps).thenReturn(List.of());

            assertThatThrownBy(initializer::initialize).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("이미 초기화되어 있으면 기존 FirebaseApp을 반환한다.")
    @Test
    void whenAlreadyInitialized_returnExistingFirebaseApp() {
        FcmInitializer initializer = new FcmInitializer("unused");

        FirebaseApp existingApp = mock(FirebaseApp.class);

        try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class)) {
            firebaseAppMock.when(FirebaseApp::getApps).thenReturn(List.of(existingApp));

            FirebaseApp initializedApp = initializer.initialize();

            assertThat(initializedApp).isSameAs(existingApp);
            firebaseAppMock.verify(
                    () -> FirebaseApp.initializeApp(any(FirebaseOptions.class)), never());
        }
    }

    @DisplayName("키 파일이 있고 초기화되지 않았으면 FirebaseApp을 초기화하고 반환한다.")
    @Test
    void whenKeyFileExistsAndNotInitialized_initializeAndReturnFirebaseApp(@TempDir Path tempDir)
            throws IOException {
        Path keyJson = tempDir.resolve("key.json");
        Files.writeString(keyJson, "{}");

        FcmInitializer initializer = new FcmInitializer(keyJson.toString());

        FirebaseApp expectedApp = mock(FirebaseApp.class);
        GoogleCredentials credentials = mock(GoogleCredentials.class);

        try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class);
                MockedStatic<GoogleCredentials> credentialsMock =
                        mockStatic(GoogleCredentials.class)) {
            firebaseAppMock.when(FirebaseApp::getApps).thenReturn(List.of());
            credentialsMock
                    .when(() -> GoogleCredentials.fromStream(any(InputStream.class)))
                    .thenReturn(credentials);
            firebaseAppMock
                    .when(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class)))
                    .thenReturn(expectedApp);

            FirebaseApp initializedApp = initializer.initialize();

            assertThat(initializedApp).isSameAs(expectedApp);
            firebaseAppMock.verify(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class)));
        }
    }
}
