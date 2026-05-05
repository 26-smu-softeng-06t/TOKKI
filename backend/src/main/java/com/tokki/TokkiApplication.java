package com.tokki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TokkiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TokkiApplication.class, args);
    }
}
