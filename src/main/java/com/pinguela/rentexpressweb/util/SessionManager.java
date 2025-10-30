package com.pinguela.rentexpressweb.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionManager {

	public static HttpSession getSession(HttpServletRequest request) {

		return request.getSession();
	}

	public static void logout(HttpServletRequest request) {
		HttpSession session = getSession(request);
		session.invalidate();

	}

	public static void setAttribute(HttpServletRequest request, String name, Object value) {
		request.getSession().setAttribute(name, value);

	}

	public static Object getAttribute(HttpServletRequest request, String name) {
		return request.getSession().getAttribute(name);
	}

	public static void removeAttribute(HttpServletRequest request, String name) {
		request.getSession().removeAttribute(name);
	}

}
