package com.swyp.server.infra.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class FcmInitializerTest {
    @DisplayName("키 파일이 없으면 IllegalStateException을 던진다.")
    @Test
    void whenKeyFileMissing_throwIllegalStateException() {
        FcmInitializer initializer = new FcmInitializer("/no/such/file/key.json");

        try (MockedStatic<FirebaseApp> firebaseAppMock = Mockito.mockStatic(FirebaseApp.class)) {
            firebaseAppMock.when(FirebaseApp::getApps).thenReturn(List.of());
            Assertions.assertThatThrownBy(initializer::initialize)
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("이미 초기화되어 있으면 기존 FirebaseApp을 반환한다.")
    @Test
    void whenAlreadyInitialized_returnExistingFirebaseApp() {
        FcmInitializer initializer = new FcmInitializer("unused");

        FirebaseApp existingApp = Mockito.mock(FirebaseApp.class);

        try (MockedStatic<FirebaseApp> firebaseAppMock = Mockito.mockStatic(FirebaseApp.class)) {
            firebaseAppMock.when(FirebaseApp::getApps).thenReturn(List.of(existingApp));

            FirebaseApp initializedApp = initializer.initialize();

            Assertions.assertThat(initializedApp).isSameAs(existingApp);
            firebaseAppMock.verify(
                    () -> FirebaseApp.initializeApp(Mockito.any(FirebaseOptions.class)),
                    Mockito.never());
        }
    }

    @DisplayName("키 파일이 있고 초기화되지 않았으면 FirebaseApp을 초기화하고 반환한다.")
    @Test
    void whenKeyFileExistsAndNotInitialized_initializeAndReturnFirebaseApp(@TempDir Path tempDir)
            throws IOException {
        Path keyJson = tempDir.resolve("key.json");
        Files.writeString(keyJson, "{}");

        FcmInitializer initializer = new FcmInitializer(keyJson.toString());

        FirebaseApp expectedApp = Mockito.mock(FirebaseApp.class);
        GoogleCredentials credentials = Mockito.mock(GoogleCredentials.class);

        try (MockedStatic<FirebaseApp> firebaseAppMock = Mockito.mockStatic(FirebaseApp.class);
                MockedStatic<GoogleCredentials> credentialsMock =
                        Mockito.mockStatic(GoogleCredentials.class)) {
            firebaseAppMock.when(FirebaseApp::getApps).thenReturn(List.of());
            credentialsMock
                    .when(() -> GoogleCredentials.fromStream(Mockito.any(InputStream.class)))
                    .thenReturn(credentials);
            firebaseAppMock
                    .when(() -> FirebaseApp.initializeApp(Mockito.any(FirebaseOptions.class)))
                    .thenReturn(expectedApp);

            FirebaseApp initializedApp = initializer.initialize();

            Assertions.assertThat(initializedApp).isSameAs(expectedApp);
            firebaseAppMock.verify(
                    () -> FirebaseApp.initializeApp(Mockito.any(FirebaseOptions.class)),
                    Mockito.times(1));
        }
    }
}
