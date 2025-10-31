package com.pinguela.rentexpressweb.security;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.CredentialStore;
import com.pinguela.rentexpressweb.security.EmployeeSessionResolver;
import com.pinguela.rentexpressweb.util.SessionUtils;
import com.pinguela.rentexpressweb.util.CookieUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

/**
 * Gestiona la cookie "remember me" y su sincronización con la sesión.
 */
public final class RememberMeCookies {

    private RememberMeCookies() {
    }

    public static void store(HttpServletRequest request, HttpServletResponse response, String email) {
        if (response == null) {
            return;
        }
        String normalized = normalize(email);
        if (normalized == null) {
            return;
        }
        boolean secure = request != null && request.isSecure();
        String encoded = Base64.getEncoder().encodeToString(normalized.getBytes(StandardCharsets.UTF_8));
        CookieUtils.addCookie(response, SecurityConstants.REMEMBER_ME_COOKIE, encoded,
                SecurityConstants.REMEMBER_ME_MAX_AGE, SecurityConstants.COOKIE_PATH, true, secure);
    }

    public static void clear(HttpServletRequest request, HttpServletResponse response) {
        if (response == null) {
            return;
        }
        boolean secure = request != null && request.isSecure();
        CookieUtils.removeCookie(response, SecurityConstants.REMEMBER_ME_COOKIE, SecurityConstants.COOKIE_PATH, secure);
    }

    public static String resolve(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Cookie rememberCookie = CookieUtils.findCookie(request, SecurityConstants.REMEMBER_ME_COOKIE);
        if (rememberCookie == null) {
            return null;
        }
        String decoded = decode(rememberCookie.getValue());
        String normalized = normalize(decoded);
        if (normalized == null) {
            return null;
        }
        if (!CredentialStore.isKnownEmail(request.getServletContext(), normalized)) {
            return null;
        }
        return normalized;
    }

    public static void syncSession(HttpServletRequest request) {
        if (request == null) {
            return;
        }
        Object currentUser = SessionUtils.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser instanceof String) {
            String normalized = normalize(currentUser.toString());
            if (normalized == null || !CredentialStore.isKnownEmail(request.getServletContext(), normalized)) {
                SessionUtils.removeAttribute(request, AppConstants.ATTR_CURRENT_USER);
                SessionUtils.removeAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
                currentUser = null;
            }
        }
        if (currentUser != null) {
            EmployeeSessionResolver.refresh(request);
            return;
        }
        String remembered = resolve(request);
        if (remembered != null) {
            SessionUtils.setAttribute(request, AppConstants.ATTR_CURRENT_USER, remembered);
            request.setAttribute(AppConstants.ATTR_REMEMBERED_EMAIL, remembered);
            EmployeeSessionResolver.resolveFromEmail(request, remembered);
        }
    }

    private static String decode(String value) {
        if (value == null) {
            return null;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(value);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static String normalize(String email) {
        if (email == null) {
            return null;
        }
        String trimmed = email.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }
}
