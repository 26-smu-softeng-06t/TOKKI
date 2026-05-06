package com.tokki.domain;

public enum DifficultyLevel {
    low, medium, high;

    public static DifficultyLevel from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "easy", "low" -> low;
            case "medium", "middle" -> medium;
            case "hard", "high" -> high;
            default -> throw new IllegalArgumentException("Unsupported difficulty: " + value);
        };
    }
}
