package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/private/UsuarioServlet")
public class PrivateUsuarioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		//
		// Verificar si el usuario ha iniciado sesión
		HttpSession session = request.getSession(false);
		// Si no hay sesión o no hay usuario en la sesión, redirigir al login
                if (session == null || session.getAttribute("employee") == null) {
			// Redirigir a la página de inicio de sesión
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			return;
		}
		// LOGOUT
		String action = request.getParameter("action");
		if ("logout".equals(action)) {
			session.invalidate(); // Invalida la sesión
                        response.sendRedirect(request.getContextPath() + "/index.jsp"); // Redirige a /index.jsp
			return;
		}

		request.getRequestDispatcher(Views.EMPLOYEE_DETAIL).forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		doGet(request, response);
	}
}
