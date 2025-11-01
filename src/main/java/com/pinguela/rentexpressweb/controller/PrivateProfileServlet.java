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

@WebServlet("/app/users/private")
public class PrivateProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String KEY_FLASH_SUCCESS = "user.profile.flash.success";
    private static final String KEY_ERROR_FULL_NAME_REQUIRED = "error.validation.fullNameRequired";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (SessionManager.get(request, AppConstants.ATTR_CURRENT_USER) == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (SessionManager.get(request, AppConstants.ATTR_CURRENT_USER) == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        Map<String, String> errors = new LinkedHashMap<String, String>();
        Map<String, String> form = new LinkedHashMap<String, String>();

        String fullName = normalize(request.getParameter(UserConstants.PARAM_FULL_NAME));
        String phone = normalize(request.getParameter(UserConstants.PARAM_PHONE));

        if (fullName == null) {
            errors.put(UserConstants.PARAM_FULL_NAME,
                    MessageResolver.getMessage(request, KEY_ERROR_FULL_NAME_REQUIRED));
        } else {
            form.put(UserConstants.PARAM_FULL_NAME, fullName);
        }
        if (phone != null) {
            form.put(UserConstants.PARAM_PHONE, phone);
        }

        if (!errors.isEmpty()) {
            request.setAttribute(UserConstants.ATTR_PROFILE_ERRORS, errors);
            request.setAttribute(UserConstants.ATTR_PROFILE_FORM, form);
            request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
            return;
        }

        SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                MessageResolver.getMessage(request, KEY_FLASH_SUCCESS));
        response.sendRedirect(request.getContextPath() + Views.PRIVATE_USER_PROFILE);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
