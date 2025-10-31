package com.pinguela.rentexpressweb.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Simple SHA-256 encoder used to hash passwords without external libraries.
 */
public final class PasswordEncoder {

    private PasswordEncoder() {
    }

    public static String hash(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        MessageDigest digest = getDigest();
        byte[] encoded = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encoded);
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        return hash(rawPassword).equals(hashedPassword);
    }

    private static MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }
}
