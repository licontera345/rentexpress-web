package com.pinguela.rentexpressweb.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionManager {

	public static HttpSession getSession(HttpServletRequest request) {

		return request.getSession();
	}

        public static void logout(HttpServletRequest request) {
                HttpSession session = request.getSession(false);

                if (session != null) {
                        session.invalidate();
                }
        }

	public static void setAttribute(HttpServletRequest request, String name, Object value) {
		request.getSession().setAttribute(name, value);

	}

        public static Object getAttribute(HttpServletRequest request, String name) {
                HttpSession session = request.getSession(false);

                if (session == null) {
                        return null;
                }

                return session.getAttribute(name);
        }

        public static void removeAttribute(HttpServletRequest request, String name) {
                HttpSession session = request.getSession(false);

                if (session != null) {
                        session.removeAttribute(name);
                }
        }

}
