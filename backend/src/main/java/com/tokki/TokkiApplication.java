package com.tokki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TokkiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TokkiApplication.class, args);
    }
}
