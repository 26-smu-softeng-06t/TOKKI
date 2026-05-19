package com.tokki.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.client-email}")
    private String clientEmail;

    @Value("${firebase.private-key}")
    private String privateKey;

    @PostConstruct
    public void initializeFirebase() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        if (!StringUtils.hasText(projectId) || !StringUtils.hasText(clientEmail) || !StringUtils.hasText(privateKey)) {
            log.warn("Firebase credentials not configured — Firebase auth disabled");
            return;
        }
        String json = buildServiceAccountJson(projectId, clientEmail, privateKey);
        InputStream serviceAccount = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId(projectId)
                .build();
        FirebaseApp.initializeApp(options);
        log.info("Firebase initialized for project: {}", projectId);
    }

    private String buildServiceAccountJson(String projectId, String clientEmail, String privateKey) {
        // Normalize escaped newlines from env var to actual newlines for Gson serialization
        String normalizedKey = privateKey.replace("\\n", "\n");
        JsonObject json = new JsonObject();
        json.addProperty("type", "service_account");
        json.addProperty("project_id", projectId);
        json.addProperty("private_key_id", "");
        json.addProperty("private_key", normalizedKey);
        json.addProperty("client_email", clientEmail);
        json.addProperty("client_id", "");
        json.addProperty("auth_uri", "https://accounts.google.com/o/oauth2/auth");
        json.addProperty("token_uri", "https://oauth2.googleapis.com/token");
        return json.toString();
    }
}
