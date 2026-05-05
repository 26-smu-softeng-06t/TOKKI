package com.tokki.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.AccessToken;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.client-email}")
    private String clientEmail;

    @Value("${firebase.private-key}")
    private String privateKey;

    @Bean
    public FirebaseApp firebaseApp() throws Exception {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }
        if (projectId.isBlank() || clientEmail.isBlank() || privateKey.isBlank()) {
            GoogleCredentials localCredentials = GoogleCredentials.create(
                new AccessToken("local-dev-token", Date.from(Instant.now().plusSeconds(3600)))
            );
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(localCredentials)
                .setProjectId("tokki-local")
                .build();
            return FirebaseApp.initializeApp(options);
        }
        String credentialsJson = """
            {
              "type": "service_account",
              "project_id": "%s",
              "client_email": "%s",
              "private_key": "%s"
            }
            """.formatted(projectId, clientEmail, privateKey.replace("\\n", "\\\\n"));
        GoogleCredentials credentials = GoogleCredentials.fromStream(
            new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))
        );
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp app) {
        return FirebaseAuth.getInstance(app);
    }
}
