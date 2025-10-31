package com.pinguela.rentexpressweb.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SessionManager {

    private SessionManager() {
    }

    public static HttpSession getSession(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getSession(false);
    }

    public static Object get(HttpServletRequest request, String name) {
        HttpSession session = getSession(request);
        return session != null ? session.getAttribute(name) : null;
    }

    public static void set(HttpServletRequest request, String name, Object value) {
        if (request == null) {
            return;
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(name, value);
    }

    public static void remove(HttpServletRequest request, String name) {
        HttpSession session = getSession(request);
        if (session != null) {
            session.removeAttribute(name);
        }
    }

    public static void invalidate(HttpServletRequest request) {
        HttpSession session = getSession(request);
        if (session != null) {
            session.invalidate();
        }
    }
}
