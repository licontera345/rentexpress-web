package com.pinguela.rentexpressweb.security;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

import com.pinguela.rentexpressweb.constants.SecurityConstants;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Gestiona el ciclo de vida de los códigos de restablecimiento de contraseña.
 */
public final class PasswordResetManager {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ATTR_RESET_EMAIL = "passwordResetEmail";
    private static final String ATTR_RESET_CODE = "passwordResetCode";
    private static final String ATTR_RESET_EXPIRATION = "passwordResetExpiration";
    private static final String ATTR_RESET_VERIFIED = "passwordResetVerified";

    private PasswordResetManager() {
    }

    public static String initiate(HttpServletRequest request, String email) {
        String code = generateCode();
        long expiration = Instant.now()
                .plusSeconds(SecurityConstants.TWO_FA_CODE_VALIDITY_SECONDS)
                .toEpochMilli();
        SessionManager.setAttribute(request, ATTR_RESET_EMAIL, email);
        SessionManager.setAttribute(request, ATTR_RESET_CODE, code);
        SessionManager.setAttribute(request, ATTR_RESET_EXPIRATION, expiration);
        SessionManager.setAttribute(request, ATTR_RESET_VERIFIED, Boolean.FALSE);
        return code;
    }

    public static boolean hasPending(HttpServletRequest request) {
        return SessionManager.getAttribute(request, ATTR_RESET_EMAIL) != null
                && SessionManager.getAttribute(request, ATTR_RESET_CODE) != null;
    }

    public static String regenerate(HttpServletRequest request) {
        if (!hasPending(request)) {
            return null;
        }
        return initiate(request, getPendingEmail(request));
    }

    public static String getPendingEmail(HttpServletRequest request) {
        Object value = SessionManager.getAttribute(request, ATTR_RESET_EMAIL);
        return value != null ? value.toString() : null;
    }

    public static boolean isExpired(HttpServletRequest request) {
        Instant expiration = resolveExpiration(request);
        if (expiration == null) {
            return true;
        }
        return Instant.now().isAfter(expiration);
    }

    public static long secondsUntilExpiration(HttpServletRequest request) {
        Instant expiration = resolveExpiration(request);
        if (expiration == null) {
            return 0L;
        }
        long seconds = Duration.between(Instant.now(), expiration).getSeconds();
        return Math.max(0L, seconds);
    }

    public static boolean matchesCode(HttpServletRequest request, String providedCode) {
        Object expected = SessionManager.getAttribute(request, ATTR_RESET_CODE);
        return expected != null && expected.toString().equals(providedCode);
    }

    public static void markVerified(HttpServletRequest request) {
        SessionManager.setAttribute(request, ATTR_RESET_VERIFIED, Boolean.TRUE);
    }

    public static boolean canReset(HttpServletRequest request) {
        Object verified = SessionManager.getAttribute(request, ATTR_RESET_VERIFIED);
        return Boolean.TRUE.equals(verified) && getPendingEmail(request) != null;
    }

    public static void clear(HttpServletRequest request) {
        SessionManager.removeAttribute(request, ATTR_RESET_EMAIL);
        SessionManager.removeAttribute(request, ATTR_RESET_CODE);
        SessionManager.removeAttribute(request, ATTR_RESET_EXPIRATION);
        SessionManager.removeAttribute(request, ATTR_RESET_VERIFIED);
    }

    private static Instant resolveExpiration(HttpServletRequest request) {
        Object value = SessionManager.getAttribute(request, ATTR_RESET_EXPIRATION);
        if (value instanceof Long) {
            return Instant.ofEpochMilli(((Long) value).longValue());
        }
        if (value instanceof Instant) {
            return (Instant) value;
        }
        if (value instanceof String) {
            try {
                return Instant.ofEpochMilli(Long.parseLong((String) value));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private static String generateCode() {
        int bound = (int) Math.pow(10, SecurityConstants.TWO_FA_CODE_LENGTH);
        int number = RANDOM.nextInt(bound);
        return String.format("%0" + SecurityConstants.TWO_FA_CODE_LENGTH + "d", number);
    }
}
