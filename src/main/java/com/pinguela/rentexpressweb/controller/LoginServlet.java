package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.security.CookieManager;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(LoginServlet.class);
    private static final String KEY_ERROR_EMAIL_REQUIRED = "error.validation.emailRequired";
    private static final String KEY_ERROR_PASSWORD_REQUIRED = "error.validation.passwordRequired";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute(AppConstants.ATTR_LOGIN_EMAIL) == null) {
            Cookie remembered = CookieManager.getCookie(request, AppConstants.COOKIE_REMEMBER_USER);
            if (remembered != null) {
                request.setAttribute(AppConstants.ATTR_LOGIN_EMAIL, remembered.getValue());
            }
        }
        request.getRequestDispatcher(Views.PUBLIC_LOGIN).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> errors = new LinkedHashMap<String, String>();

        String email = normalize(request.getParameter(UserConstants.PARAM_EMAIL));
        String password = normalize(request.getParameter(UserConstants.PARAM_PASSWORD));
        boolean remember = request.getParameter(AppConstants.PARAM_REMEMBER_ME) != null;

        if (email == null) {
            errors.put(UserConstants.PARAM_EMAIL, MessageResolver.getMessage(request, KEY_ERROR_EMAIL_REQUIRED));
        }
        if (password == null) {
            errors.put(UserConstants.PARAM_PASSWORD, MessageResolver.getMessage(request, KEY_ERROR_PASSWORD_REQUIRED));
        }

        if (!errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            request.setAttribute(AppConstants.ATTR_LOGIN_EMAIL, email);
            request.getRequestDispatcher(Views.PUBLIC_LOGIN).forward(request, response);
            return;
        }

        LOGGER.info("Usuario {} autenticado", email);
        SessionManager.set(request, AppConstants.ATTR_CURRENT_USER, email);
        SessionManager.remove(request, AppConstants.ATTR_CURRENT_EMPLOYEE);

        if (remember) {
            CookieManager.addCookie(response, AppConstants.COOKIE_REMEMBER_USER, email, 7);
        } else {
            Cookie cookie = CookieManager.getCookie(request, AppConstants.COOKIE_REMEMBER_USER);
            if (cookie != null) {
                CookieManager.removeCookie(response, AppConstants.COOKIE_REMEMBER_USER);
            }
        }

        response.sendRedirect(request.getContextPath() + Views.PRIVATE_USER_HOME);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
