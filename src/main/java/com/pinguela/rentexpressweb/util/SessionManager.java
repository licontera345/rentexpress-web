package com.pinguela.rentexpressweb.util;

import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpressweb.constants.AppConstants;

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

    public static Object getAttribute(HttpServletRequest request, String name) {
        return get(request, name);
    }

    public static void set(HttpServletRequest request, String name, Object value) {
        if (request == null) {
            return;
        }
        HttpSession session = getSession(request);
        if (session == null) {
            session = request.getSession(true);
        }
        session.setAttribute(name, value);
    }

    public static void remove(HttpServletRequest request, String name) {
        if (request == null) {
            return;
        }
        HttpSession session = getSession(request);
        if (session != null) {
            session.removeAttribute(name);
        }
    }

    public static void invalidate(HttpServletRequest request) {
        if (request == null) {
            return;
        }
        HttpSession session = getSession(request);
        if (session != null) {
            session.invalidate();
        }
    }

    public static UserDTO getLoggedUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return getLoggedUser(request.getSession(false));
    }

    public static UserDTO getLoggedUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(AppConstants.ATTR_CURRENT_USER);
        if (value instanceof UserDTO) {
            return (UserDTO) value;
        }
        return null;
    }
}
