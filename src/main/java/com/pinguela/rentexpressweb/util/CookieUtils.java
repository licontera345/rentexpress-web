package com.pinguela.rentexpressweb.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Utilidades para el manejo de cookies.
 */
public final class CookieUtils {

    private CookieUtils() {
    }

    public static Cookie findCookie(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            if (cookie != null && name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    public static void addCookie(HttpServletResponse response, String name, String value,
                                 int maxAge, String path, boolean httpOnly, boolean secure) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path != null ? path : "/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        response.addCookie(cookie);
    }

    public static void removeCookie(HttpServletResponse response, String name, String path, boolean secure) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath(path != null ? path : "/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        response.addCookie(cookie);
    }
}
