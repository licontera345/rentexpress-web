package com.pinguela.rentexpressweb.security;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Gestión muy básica de la funcionalidad "remember me".
 */
public final class RememberMeManager {

    private RememberMeManager() {
    }

    public static void rememberUser(HttpServletResponse response, String email) {
        if (response == null || email == null || email.trim().isEmpty()) {
            return;
        }
        String normalized = email.trim();
        String encoded = Base64.getEncoder().encodeToString(normalized.getBytes(StandardCharsets.UTF_8));
        CookieUtils.addCookie(response, SecurityConstants.REMEMBER_ME_COOKIE, encoded,
                SecurityConstants.REMEMBER_ME_MAX_AGE, SecurityConstants.COOKIE_PATH, true, false);
    }

    public static void forgetUser(HttpServletResponse response) {
        CookieUtils.removeCookie(response, SecurityConstants.REMEMBER_ME_COOKIE, SecurityConstants.COOKIE_PATH);
    }

    public static String resolveRememberedUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String decoded = CookieUtils.findCookie(request, SecurityConstants.REMEMBER_ME_COOKIE)
                .map(cookie -> decode(cookie.getValue()))
                .orElse(null);
        if (decoded == null) {
            return null;
        }
        String normalized = decoded.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        if (!CredentialStore.isKnownEmail(request.getServletContext(), normalized)) {
            return null;
        }
        return normalized;
    }

    public static void applyRememberedUser(HttpServletRequest request) {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser instanceof String) {
            String normalized = ((String) currentUser).trim();
            if (normalized.isEmpty() || !CredentialStore.isKnownEmail(request.getServletContext(), normalized)) {
                SessionManager.removeAttribute(request, AppConstants.ATTR_CURRENT_USER);
                SessionManager.removeAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
                currentUser = null;
            }
        }
        if (currentUser != null) {
            EmployeeSessionResolver.refresh(request);
            return;
        }
        String remembered = resolveRememberedUser(request);
        if (remembered != null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_CURRENT_USER, remembered);
            request.setAttribute(AppConstants.ATTR_REMEMBERED_EMAIL, remembered);
            EmployeeSessionResolver.resolveFromEmail(request, remembered);
        }
    }

    private static String decode(String value) {
        try {
            return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
