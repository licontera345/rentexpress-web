package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.util.SessionUtils;
import com.pinguela.rentexpressweb.util.LegacyDateUtils;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void forward(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        if (view == null) {
            throw new IllegalArgumentException("View path must not be null");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher(view);
        if (dispatcher == null) {
            throw new ServletException("No RequestDispatcher for view: " + view);
        }
        dispatcher.forward(request, response);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String location) throws IOException {
        if (location == null) {
            throw new IllegalArgumentException("Redirect location must not be null");
        }
        if (location.startsWith("http://") || location.startsWith("https://")) {
            response.sendRedirect(location);
            return;
        }
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && location.startsWith(contextPath)) {
            response.sendRedirect(location);
        } else {
            response.sendRedirect((contextPath != null ? contextPath : "") + location);
        }
    }

    protected void disableCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    protected boolean requireUser(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws IOException {
        return requireAttribute(request, response, AppConstants.ATTR_CURRENT_USER, errorMessage,
                SecurityConstants.LOGIN_ENDPOINT);
    }

    protected boolean requireEmployee(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws IOException {
        return requireAttribute(request, response, AppConstants.ATTR_CURRENT_EMPLOYEE, errorMessage,
                SecurityConstants.HOME_ENDPOINT);
    }

    private boolean requireAttribute(HttpServletRequest request, HttpServletResponse response, String attributeName,
            String errorMessage, String redirectPath) throws IOException {
        Object attribute = SessionUtils.getAttribute(request, attributeName);
        if (attribute != null) {
            return true;
        }
        if (errorMessage != null && !errorMessage.isEmpty()) {
            SessionUtils.setAttribute(request, AppConstants.ATTR_FLASH_ERROR, errorMessage);
        }
        redirect(request, response, redirectPath);
        return false;
    }

    protected void exposeFlashMessages(HttpServletRequest request) {
        transferFlashAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        transferFlashAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        transferFlashAttribute(request, AppConstants.ATTR_FLASH_INFO);
    }

    private void transferFlashAttribute(HttpServletRequest request, String attributeName) {
        Object value = SessionUtils.getAttribute(request, attributeName);
        if (value != null) {
            request.setAttribute(attributeName, value);
            SessionUtils.removeAttribute(request, attributeName);
        }
    }

    protected String getTrimmedParameter(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            return null;
        }
        return trim(request.getParameter(name));
    }

    protected String trim(String value) {
        return value != null ? value.trim() : null;
    }

    protected String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    protected Integer parseInteger(String value, List<String> errors, String message) {
        String sanitized = trimToNull(value);
        if (sanitized == null) {
            return null;
        }
        try {
            return Integer.valueOf(sanitized);
        } catch (NumberFormatException ex) {
            addError(errors, message);
            return null;
        }
    }

    protected Date parseIsoDate(String value, List<String> errors, String message) {
        String sanitized = trimToNull(value);
        if (sanitized == null) {
            return null;
        }
        Date parsed = LegacyDateUtils.parseIsoDate(sanitized);
        if (parsed == null) {
            addError(errors, message);
        }
        return parsed;
    }

    protected BigDecimal parseDecimal(String value, List<String> errors, String invalidMessage, String negativeMessage) {
        String sanitized = trimToNull(value);
        if (sanitized == null) {
            return null;
        }
        try {
            BigDecimal decimal = new BigDecimal(sanitized);
            if (negativeMessage != null && decimal.compareTo(BigDecimal.ZERO) < 0) {
                addError(errors, negativeMessage);
                return null;
            }
            return decimal;
        } catch (NumberFormatException ex) {
            addError(errors, invalidMessage);
            return null;
        }
    }

    protected void addError(List<String> errors, String message) {
        if (errors != null && message != null && !message.isEmpty()) {
            errors.add(message);
        }
    }
}
