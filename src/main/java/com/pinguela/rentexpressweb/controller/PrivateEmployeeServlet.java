package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/private/EmployeeServlet")
public class PrivateEmployeeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

                //
                // Verificar si el empleado ha iniciado sesión
                HttpSession session = request.getSession(false);
                // Si no hay sesión o no hay empleado en la sesión, redirigir al inicio público
                if (session == null || session.getAttribute("employee") == null) {
                        response.sendRedirect(request.getContextPath() + Views.INDEX);
                        return;
                }
                // LOGOUT
                String action = request.getParameter("action");
                if ("logout".equals(action)) {
                        session.invalidate(); // Invalida la sesión
                        response.sendRedirect(request.getContextPath() + Views.INDEX); // Redirige a /public/index.jsp (Views.INDEX ya incluye el prefijo público)
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
