package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/employees/register")
public class RegisterEmployeeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String KEY_FLASH_SUCCESS = "register.employee.flash.success";
    private static final String KEY_ERROR_FULL_NAME_REQUIRED = "error.validation.fullNameRequired";
    private static final String KEY_ERROR_EMAIL_REQUIRED = "error.validation.emailRequired";
    private static final String KEY_ERROR_HEADQUARTERS_REQUIRED = "error.validation.headquartersRequired";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(Views.PUBLIC_REGISTER_EMPLOYEE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> errors = new LinkedHashMap<String, String>();
        Map<String, String> form = new LinkedHashMap<String, String>();

        String fullName = normalize(request.getParameter(UserConstants.PARAM_FULL_NAME));
        String email = normalize(request.getParameter(UserConstants.PARAM_EMAIL));
        String headquarters = normalize(request.getParameter(EmployeeConstants.PARAM_HEADQUARTERS));

        if (fullName == null) {
            errors.put(UserConstants.PARAM_FULL_NAME,
                    MessageResolver.getMessage(request, KEY_ERROR_FULL_NAME_REQUIRED));
        } else {
            form.put(UserConstants.PARAM_FULL_NAME, fullName);
        }
        if (email == null) {
            errors.put(UserConstants.PARAM_EMAIL, MessageResolver.getMessage(request, KEY_ERROR_EMAIL_REQUIRED));
        } else {
            form.put(UserConstants.PARAM_EMAIL, email);
        }
        if (headquarters == null) {
            errors.put(EmployeeConstants.PARAM_HEADQUARTERS,
                    MessageResolver.getMessage(request, KEY_ERROR_HEADQUARTERS_REQUIRED));
        } else {
            form.put(EmployeeConstants.PARAM_HEADQUARTERS, headquarters);
        }

        if (!errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            request.setAttribute(AppConstants.ATTR_FORM_DATA, form);
            request.getRequestDispatcher(Views.PUBLIC_REGISTER_EMPLOYEE).forward(request, response);
            return;
        }

        SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                MessageResolver.getMessage(request, KEY_FLASH_SUCCESS));
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
