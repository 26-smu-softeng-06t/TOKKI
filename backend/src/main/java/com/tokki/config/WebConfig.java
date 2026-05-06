package com.tokki.config;

import com.tokki.config.properties.TokkiFrontendProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TokkiFrontendProperties frontendProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(frontendOrigins())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    private String[] frontendOrigins() {
        Set<String> origins = new LinkedHashSet<>();
        addOrigin(origins, frontendProperties.loginUrl());
        addOrigin(origins, frontendProperties.callbackUrl());
        origins.add("http://localhost:5173");
        origins.add("http://127.0.0.1:5173");
        return origins.toArray(String[]::new);
    }

    private static void addOrigin(Set<String> origins, String url) {
        if (url == null || url.isBlank()) {
            return;
        }

        URI uri = URI.create(url);
        int port = uri.getPort();
        String origin = uri.getScheme() + "://" + uri.getHost() + (port == -1 ? "" : ":" + port);
        origins.add(origin);
    }
}
