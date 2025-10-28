package com.pinguela.rentexpressweb.security;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.util.CookieUtils;
import jakarta.servlet.http.Cookie;
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
        String encoded = Base64.getEncoder().encodeToString(email.getBytes(StandardCharsets.UTF_8));
        CookieUtils.addCookie(response, SecurityConstants.REMEMBER_ME_COOKIE, encoded,
                SecurityConstants.REMEMBER_ME_MAX_AGE, SecurityConstants.COOKIE_PATH, true, false);
    }

    public static void forgetUser(HttpServletResponse response) {
        CookieUtils.removeCookie(response, SecurityConstants.REMEMBER_ME_COOKIE, SecurityConstants.COOKIE_PATH);
    }

    public static String resolveRememberedUser(HttpServletRequest request) {
        Cookie cookie = CookieUtils.findCookie(request, SecurityConstants.REMEMBER_ME_COOKIE);
        if (cookie == null) {
            return null;
        }
        return decode(cookie.getValue());
    }

    public static void applyRememberedUser(HttpServletRequest request) {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser != null) {
            return;
        }
        String remembered = resolveRememberedUser(request);
        if (remembered != null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_CURRENT_USER, remembered);
            request.setAttribute(AppConstants.ATTR_REMEMBERED_EMAIL, remembered);
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
