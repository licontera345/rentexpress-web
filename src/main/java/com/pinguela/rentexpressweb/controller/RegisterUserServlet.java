package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/users/register")
public class RegisterUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String MESSAGE_REGISTERED = "Cuenta creada correctamente. Inicia sesión para continuar.";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> errors = new LinkedHashMap<String, String>();
        Map<String, String> form = new LinkedHashMap<String, String>();

        String email = normalize(request.getParameter(UserConstants.PARAM_EMAIL));
        String password = normalize(request.getParameter(UserConstants.PARAM_PASSWORD));
        String confirmPassword = normalize(request.getParameter(UserConstants.PARAM_CONFIRM_PASSWORD));

        if (email == null) {
            errors.put(UserConstants.PARAM_EMAIL, "El correo electrónico es obligatorio.");
        } else {
            form.put(UserConstants.PARAM_EMAIL, email);
        }
        if (password == null) {
            errors.put(UserConstants.PARAM_PASSWORD, "La contraseña es obligatoria.");
        }
        if (confirmPassword == null) {
            errors.put(UserConstants.PARAM_CONFIRM_PASSWORD, "Confirma la contraseña.");
        } else if (password != null && !password.equals(confirmPassword)) {
            errors.put(UserConstants.PARAM_CONFIRM_PASSWORD, "Las contraseñas no coinciden.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            request.setAttribute(AppConstants.ATTR_FORM_DATA, form);
            request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
            return;
        }

        SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS, MESSAGE_REGISTERED);
        response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
