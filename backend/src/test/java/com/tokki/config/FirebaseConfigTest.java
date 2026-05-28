package com.tokki.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class FirebaseConfigTest {

    @Test
    void envPrivateKeyNormalizesEscapedNewlines() throws Exception {
        FirebaseConfig config = new FirebaseConfig();
        Method method = FirebaseConfig.class.getDeclaredMethod(
                "buildServiceAccountJson",
                String.class,
                String.class,
                String.class
        );
        method.setAccessible(true);

        String json = (String) method.invoke(config, "project-id", "firebase@example.com", "line1\\nline2");

        assertThat(json).contains("line1\\nline2");
    }
}
