package com.pinguela.rentexpressweb.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class CookieManager {

    private static final int SECONDS_PER_DAY = 24 * 60 * 60;

    private CookieManager() {
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int days) {
        if (response == null || name == null || value == null) {
            return;
        }
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(days * SECONDS_PER_DAY);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
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

    public static void removeCookie(HttpServletResponse response, String name) {
        if (response == null || name == null) {
            return;
        }
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }
}
