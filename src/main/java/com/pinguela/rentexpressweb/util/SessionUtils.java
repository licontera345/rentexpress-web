package com.pinguela.rentexpressweb.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SessionUtils {

    private SessionUtils() {
    }

    public static HttpSession getSession(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getSession(false);
    }

    public static HttpSession getOrCreateSession(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getSession(true);
    }

    public static void logout(HttpServletRequest request) {
        HttpSession session = getSession(request);
        if (session != null) {
            session.invalidate();
        }
    }

    public static void setAttribute(HttpServletRequest request, String name, Object value) {
        if (request == null) {
            return;
        }
        HttpSession session = getOrCreateSession(request);
        session.setAttribute(name, value);
    }

    public static Object getAttribute(HttpServletRequest request, String name) {
        HttpSession session = getSession(request);
        if (session == null) {
            return null;
        }
        return session.getAttribute(name);
    }

    public static void removeAttribute(HttpServletRequest request, String name) {
        HttpSession session = getSession(request);
        if (session != null) {
            session.removeAttribute(name);
        }
    }
}
