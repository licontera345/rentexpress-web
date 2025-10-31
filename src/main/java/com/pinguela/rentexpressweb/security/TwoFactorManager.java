package com.pinguela.rentexpressweb.security;

import java.security.SecureRandom;

import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.util.SessionUtils;

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
        long expiration = System.currentTimeMillis()
                + (SecurityConstants.TWO_FA_CODE_VALIDITY_SECONDS * 1000L);
        SessionUtils.setAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EMAIL, email);
        SessionUtils.setAttribute(request, SecurityConstants.ATTR_PENDING_2FA_CODE, code);
        SessionUtils.setAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EXPIRATION, expiration);
        SessionUtils.setAttribute(request, SecurityConstants.ATTR_PENDING_2FA_REMEMBER, Boolean.valueOf(remember));
        return code;
    }

    public static boolean hasPendingVerification(HttpServletRequest request) {
        return SessionUtils.getAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EMAIL) != null
                && SessionUtils.getAttribute(request, SecurityConstants.ATTR_PENDING_2FA_CODE) != null;
    }

    public static String regenerate(HttpServletRequest request) {
        if (!hasPendingVerification(request)) {
            return null;
        }
        return initiate(request, getPendingEmail(request), shouldRemember(request));
    }

    public static String getPendingEmail(HttpServletRequest request) {
        Object value = SessionUtils.getAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EMAIL);
        return value != null ? value.toString() : null;
    }

    public static boolean shouldRemember(HttpServletRequest request) {
        Object value = SessionUtils.getAttribute(request, SecurityConstants.ATTR_PENDING_2FA_REMEMBER);
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return false;
    }

    public static boolean isExpired(HttpServletRequest request) {
        Long expiration = resolveExpirationMillis(request);
        if (expiration == null) {
            return true;
        }
        return System.currentTimeMillis() > expiration.longValue();
    }

    public static long secondsUntilExpiration(HttpServletRequest request) {
        Long expiration = resolveExpirationMillis(request);
        if (expiration == null) {
            return 0L;
        }
        long remaining = expiration.longValue() - System.currentTimeMillis();
        if (remaining <= 0L) {
            return 0L;
        }
        return remaining / 1000L;
    }

    public static boolean matchesCode(HttpServletRequest request, String providedCode) {
        Object expected = SessionUtils.getAttribute(request, SecurityConstants.ATTR_PENDING_2FA_CODE);
        return expected != null && expected.toString().equals(providedCode);
    }

    public static void clear(HttpServletRequest request) {
        SessionUtils.removeAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EMAIL);
        SessionUtils.removeAttribute(request, SecurityConstants.ATTR_PENDING_2FA_CODE);
        SessionUtils.removeAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EXPIRATION);
        SessionUtils.removeAttribute(request, SecurityConstants.ATTR_PENDING_2FA_REMEMBER);
    }

    private static String generateCode() {
        int bound = (int) Math.pow(10, SecurityConstants.TWO_FA_CODE_LENGTH);
        int number = RANDOM.nextInt(bound);
        return String.format("%0" + SecurityConstants.TWO_FA_CODE_LENGTH + "d", number);
    }

    private static Long resolveExpirationMillis(HttpServletRequest request) {
        Object value = SessionUtils.getAttribute(request, SecurityConstants.ATTR_PENDING_2FA_EXPIRATION);
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof String) {
            try {
                long millis = Long.parseLong((String) value);
                return Long.valueOf(millis);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }
}
