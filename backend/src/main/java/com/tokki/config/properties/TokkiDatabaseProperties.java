package com.tokki.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tokki.database")
public record TokkiDatabaseProperties(
        String host,
        int port,
        String name,
        String username,
        String sslMode
) {
}
