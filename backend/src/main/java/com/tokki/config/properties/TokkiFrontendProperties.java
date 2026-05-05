package com.tokki.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tokki.frontend")
public record TokkiFrontendProperties(String loginUrl, String callbackUrl) {
}
