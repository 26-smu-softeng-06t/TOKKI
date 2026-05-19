package com.tokki.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class FirebaseConfigTest {

    @Test
    void missingServiceAccountPathDoesNotCrashPathResolution() throws Exception {
        FirebaseConfig config = new FirebaseConfig();
        Method method = FirebaseConfig.class.getDeclaredMethod("resolveServiceAccountPath", String.class);
        method.setAccessible(true);

        Object resolved = method.invoke(config, "../missing-firebase-service-account.json");

        assertThat(resolved).isNull();
    }

    @Test
    void envPrivateKeyNormalizesEscapedNewlines() throws Exception {
        FirebaseConfig config = new FirebaseConfig();
        Method method = FirebaseConfig.class.getDeclaredMethod("buildServiceAccountJson", String.class, String.class, String.class);
        method.setAccessible(true);

        String json = (String) method.invoke(config, "project-id", "firebase@example.com", "line1\\nline2");

        assertThat(json).contains("line1\\nline2");
    }
}
