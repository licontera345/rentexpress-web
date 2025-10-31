package com.pinguela.rentexpressweb.security;

import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Valida los parámetros recibidos al intentar autenticarse.
 */
public final class LoginRequestValidator {

    private static final String MESSAGE_KEY_EMAIL_REQUIRED = "login.error.requiredEmail";
    private static final String MESSAGE_KEY_PASSWORD_REQUIRED = "login.error.requiredPassword";

    private LoginRequestValidator() {
    }

    public static LoginRequest validate(HttpServletRequest request) {
        String emailParam = request.getParameter(UserConstants.PARAM_EMAIL);
        String passwordParam = request.getParameter(UserConstants.PARAM_PASSWORD);

        String sanitizedEmail = emailParam != null ? emailParam.trim() : null;
        Map<String, String> errors = new LinkedHashMap<String, String>();

        if (sanitizedEmail == null || sanitizedEmail.isEmpty()) {
            errors.put(UserConstants.PARAM_EMAIL, MessageResolver.getMessage(request, MESSAGE_KEY_EMAIL_REQUIRED));
        }
        if (passwordParam == null || passwordParam.trim().isEmpty()) {
            errors.put(UserConstants.PARAM_PASSWORD, MessageResolver.getMessage(request, MESSAGE_KEY_PASSWORD_REQUIRED));
        }

        return new LoginRequest(sanitizedEmail, passwordParam, errors);
    }
}
