package com.pinguela.rentexpressweb.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionManager {

        private SessionManager() {
        }

        public static HttpSession getSession(HttpServletRequest request) {
                if (request == null) {
                        return null;
                }
                return request.getSession(false);
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
                HttpSession session = request.getSession();
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
