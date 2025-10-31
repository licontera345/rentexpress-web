package com.pinguela.rentexpressweb.security;

import java.security.SecureRandom;

import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.util.SessionUtils;

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
        long expiration = System.currentTimeMillis()
                + (SecurityConstants.TWO_FA_CODE_VALIDITY_SECONDS * 1000L);
        SessionUtils.setAttribute(request, ATTR_RESET_EMAIL, email);
        SessionUtils.setAttribute(request, ATTR_RESET_CODE, code);
        SessionUtils.setAttribute(request, ATTR_RESET_EXPIRATION, expiration);
        SessionUtils.setAttribute(request, ATTR_RESET_VERIFIED, Boolean.FALSE);
        return code;
    }

    public static boolean hasPending(HttpServletRequest request) {
        return SessionUtils.getAttribute(request, ATTR_RESET_EMAIL) != null
                && SessionUtils.getAttribute(request, ATTR_RESET_CODE) != null;
    }

    public static String regenerate(HttpServletRequest request) {
        if (!hasPending(request)) {
            return null;
        }
        return initiate(request, getPendingEmail(request));
    }

    public static String getPendingEmail(HttpServletRequest request) {
        Object value = SessionUtils.getAttribute(request, ATTR_RESET_EMAIL);
        return value != null ? value.toString() : null;
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
        Object expected = SessionUtils.getAttribute(request, ATTR_RESET_CODE);
        return expected != null && expected.toString().equals(providedCode);
    }

    public static void markVerified(HttpServletRequest request) {
        SessionUtils.setAttribute(request, ATTR_RESET_VERIFIED, Boolean.TRUE);
    }

    public static boolean canReset(HttpServletRequest request) {
        Object verified = SessionUtils.getAttribute(request, ATTR_RESET_VERIFIED);
        return Boolean.TRUE.equals(verified) && getPendingEmail(request) != null;
    }

    public static void clear(HttpServletRequest request) {
        SessionUtils.removeAttribute(request, ATTR_RESET_EMAIL);
        SessionUtils.removeAttribute(request, ATTR_RESET_CODE);
        SessionUtils.removeAttribute(request, ATTR_RESET_EXPIRATION);
        SessionUtils.removeAttribute(request, ATTR_RESET_VERIFIED);
    }

    private static Long resolveExpirationMillis(HttpServletRequest request) {
        Object value = SessionUtils.getAttribute(request, ATTR_RESET_EXPIRATION);
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof String) {
            try {
                return Long.valueOf(Long.parseLong((String) value));
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
