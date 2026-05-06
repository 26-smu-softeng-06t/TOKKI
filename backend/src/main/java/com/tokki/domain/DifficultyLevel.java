package com.tokki.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DifficultyLevel {
    easy, medium, hard;

    @JsonCreator
    public static DifficultyLevel from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "easy", "low" -> easy;
            case "medium", "middle" -> medium;
            case "hard", "high" -> hard;
            default -> throw new IllegalArgumentException("Unsupported difficulty: " + value);
        };
    }

    @JsonValue
    public String toApiValue() {
        return name();
    }
}
