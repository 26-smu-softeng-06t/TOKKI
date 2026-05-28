package com.tokki.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tokki.admin")
public record TokkiAdminProperties(String secretKey) {
}
