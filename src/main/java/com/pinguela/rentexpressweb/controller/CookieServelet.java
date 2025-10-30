package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CookieServelet
 */
@WebServlet("/CookieServelet")
public class CookieServelet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CookieServelet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("numVisitas".equals(cookie.getName())) {
					int visitas = Integer.parseInt(cookie.getValue());
					cookie.setValue(String.valueOf((++visitas)));
					cookie.setMaxAge(5 * 60);
					response.addCookie(cookie);
				}
				response.getWriter().append(
						cookie.getName() + ":  " + cookie.getValue() + " , expira en (seg): " + cookie.getMaxAge());
			}
		} else {
			int numVisitas = 1;
			Cookie contadorVisitas = new Cookie("numVisitas", String.valueOf(numVisitas));
			contadorVisitas.setMaxAge(5 * 60);
			response.addCookie(contadorVisitas);
			response.getWriter().append("no envio cookie, primera visita");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
