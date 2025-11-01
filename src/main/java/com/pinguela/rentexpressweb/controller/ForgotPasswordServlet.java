package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/password/forgot")
public class ForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(Views.PUBLIC_FORGOT_PASSWORD).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> errors = new LinkedHashMap<String, String>();
        String email = normalize(request.getParameter(UserConstants.PARAM_EMAIL));
        if (email == null) {
            errors.put(UserConstants.PARAM_EMAIL,
                    MessageResolver.getMessage(request, "error.validation.emailRequired"));
        }
        if (!errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            request.setAttribute(AppConstants.ATTR_FORM_DATA, buildFormData(email));
            request.getRequestDispatcher(Views.PUBLIC_FORGOT_PASSWORD).forward(request, response);
            return;
        }
        SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                MessageResolver.getMessage(request, "common.password.forgot.flash.success"));
        response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
    }

    private Map<String, String> buildFormData(String email) {
        Map<String, String> data = new LinkedHashMap<String, String>();
        if (email != null) {
            data.put(UserConstants.PARAM_EMAIL, email);
        }
        return data;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
