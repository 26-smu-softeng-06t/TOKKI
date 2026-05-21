package com.tokki.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RoomCodeUtil {

    private static final String SALT = "TOKKI_PVP_SALT_2026";
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 32 chars (no I, O, 0, 1)
    private static final int CODE_LENGTH = 6;

    private static final MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static String encode(Long roomId) {
        if (roomId == null) return null;

        byte[] hash = digest.digest((SALT + roomId).getBytes(StandardCharsets.UTF_8));
        long value = ((hash[0] & 0xFFL) << 24) | ((hash[1] & 0xFFL) << 16) |
                     ((hash[2] & 0xFFL) << 8) | (hash[3] & 0xFFL);

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_CHARS.charAt((int) (value % CODE_CHARS.length())));
            value /= CODE_CHARS.length();
        }
        return code.toString();
    }

    public static Long decode(String roomCode) {
        if (roomCode == null || roomCode.length() != CODE_LENGTH) return null;

        long value = 0;
        for (int i = CODE_LENGTH - 1; i >= 0; i--) {
            char c = roomCode.charAt(i);
            int pos = CODE_CHARS.indexOf(c);
            if (pos == -1) return null; // invalid character
            value = value * CODE_CHARS.length() + pos;
        }

        // Brute force find the room ID (should be fast for reasonable room counts)
        for (long id = 1; id <= 100000; id++) {
            if (encode(id).equals(roomCode)) {
                return id;
            }
        }
        return null;
    }

    private RoomCodeUtil() {}
}
