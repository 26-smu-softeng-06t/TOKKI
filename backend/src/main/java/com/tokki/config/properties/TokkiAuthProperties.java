package com.tokki.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tokki.auth")
public record TokkiAuthProperties(EventLog eventLog) {

    public TokkiAuthProperties {
        if (eventLog == null) {
            eventLog = new EventLog(100);
        }
    }

    public record EventLog(int maxEvents) {

        public EventLog {
            if (maxEvents < 1) {
                maxEvents = 100;
            }
        }
    }
}
