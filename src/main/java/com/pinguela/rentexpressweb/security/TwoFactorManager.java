package com.pinguela.rentexpressweb.security;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

import com.pinguela.rentexpressweb.constants.SecurityConstants;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Gestiona el ciclo de vida del código de verificación en dos pasos.
 */
public final class TwoFactorManager {

    private static final SecureRandom RANDOM = new SecureRandom();

    private TwoFactorManager() {
    }

    public static String initiate(HttpServletRequest request, String email, boolean remember) {
        String code = generateCode();
        long expiration = Instant.now()
                .plusSeconds(SecurityConstants.TWO_FA_CODE_VALIDITY_SECONDS)
                .toEpochMilli();
        SessionManager.setAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EMAIL, email);
        SessionManager.setAttribute(request, SecurityConstants.ATTR_PENDING_2FA_CODE, code);
        SessionManager.setAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EXPIRATION, expiration);
        SessionManager.setAttribute(request, SecurityConstants.ATTR_PENDING_2FA_REMEMBER, Boolean.valueOf(remember));
        return code;
    }

    public static boolean hasPendingVerification(HttpServletRequest request) {
        return SessionManager.getAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EMAIL) != null
                && SessionManager.getAttribute(request, SecurityConstants.ATTR_PENDING_2FA_CODE) != null;
    }

    public static String regenerate(HttpServletRequest request) {
        if (!hasPendingVerification(request)) {
            return null;
        }
        return initiate(request, getPendingEmail(request), shouldRemember(request));
    }

    public static String getPendingEmail(HttpServletRequest request) {
        Object value = SessionManager.getAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EMAIL);
        return value != null ? value.toString() : null;
    }

    public static boolean shouldRemember(HttpServletRequest request) {
        Object value = SessionManager.getAttribute(request, SecurityConstants.ATTR_PENDING_2FA_REMEMBER);
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return false;
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
        return Math.max(seconds, 0L);
    }

    public static boolean matchesCode(HttpServletRequest request, String providedCode) {
        Object expected = SessionManager.getAttribute(request, SecurityConstants.ATTR_PENDING_2FA_CODE);
        return expected != null && expected.toString().equals(providedCode);
    }

    public static void clear(HttpServletRequest request) {
        SessionManager.removeAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EMAIL);
        SessionManager.removeAttribute(request, SecurityConstants.ATTR_PENDING_2FA_CODE);
        SessionManager.removeAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EXPIRATION);
        SessionManager.removeAttribute(request, SecurityConstants.ATTR_PENDING_2FA_REMEMBER);
    }

    private static String generateCode() {
        int bound = (int) Math.pow(10, SecurityConstants.TWO_FA_CODE_LENGTH);
        int number = RANDOM.nextInt(bound);
        return String.format("%0" + SecurityConstants.TWO_FA_CODE_LENGTH + "d", number);
    }

    private static Instant resolveExpiration(HttpServletRequest request) {
        Object value = SessionManager.getAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EXPIRATION);
        if (value instanceof Long) {
            return Instant.ofEpochMilli(((Long) value).longValue());
        }
        if (value instanceof Instant) {
            return (Instant) value;
        }
        if (value instanceof String) {
            try {
                long millis = Long.parseLong((String) value);
                return Instant.ofEpochMilli(millis);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }
}
