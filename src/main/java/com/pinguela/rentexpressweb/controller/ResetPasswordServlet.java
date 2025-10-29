package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.PasswordConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.CredentialStore;
import com.pinguela.rentexpressweb.security.PasswordResetManager;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class ResetPasswordServlet
 */
@WebServlet("/app/password/reset")
public class ResetPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(ResetPasswordServlet.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResetPasswordServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PasswordResetManager.canReset(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.reset.invalidSession"));
            response.sendRedirect(request.getContextPath() + "/app/password/forgot");
            return;
        }

        copyFlashMessages(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                MessageResolver.getMessage(request, "page.resetPassword.title"));
        request.setAttribute(PasswordConstants.ATTR_PENDING_EMAIL, PasswordResetManager.getPendingEmail(request));
        request.getRequestDispatcher(Views.PUBLIC_RESET_PASSWORD).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PasswordResetManager.canReset(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.reset.sessionExpired"));
            response.sendRedirect(request.getContextPath() + "/app/password/forgot");
            return;
        }

        String newPassword = request.getParameter(PasswordConstants.PARAM_NEW_PASSWORD);
        String confirmPassword = request.getParameter(PasswordConstants.PARAM_CONFIRM_PASSWORD);
        String sanitizedNewPassword = newPassword != null ? newPassword.trim() : null;
        String sanitizedConfirm = confirmPassword != null ? confirmPassword.trim() : null;

        Map<String, String> errors = new LinkedHashMap<String, String>();
        if (sanitizedNewPassword == null || sanitizedNewPassword.isEmpty()) {
            errors.put(PasswordConstants.PARAM_NEW_PASSWORD,
                    MessageResolver.getMessage(request, "validation.reset.newPassword.required"));
        } else if (sanitizedNewPassword.length() < 8) {
            errors.put(PasswordConstants.PARAM_NEW_PASSWORD,
                    MessageResolver.getMessage(request, "validation.reset.newPassword.length"));
        }

        if (sanitizedConfirm == null || sanitizedConfirm.isEmpty()) {
            errors.put(PasswordConstants.PARAM_CONFIRM_PASSWORD,
                    MessageResolver.getMessage(request, "validation.reset.confirm.required"));
        } else if (sanitizedNewPassword != null && !sanitizedConfirm.equals(sanitizedNewPassword)) {
            errors.put(PasswordConstants.PARAM_CONFIRM_PASSWORD,
                    MessageResolver.getMessage(request, "validation.reset.confirm.mismatch"));
        }

        if (!errors.isEmpty()) {
            request.setAttribute(PasswordConstants.ATTR_RESET_ERRORS, errors);
            request.setAttribute(PasswordConstants.ATTR_PENDING_EMAIL, PasswordResetManager.getPendingEmail(request));
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                    MessageResolver.getMessage(request, "page.resetPassword.title"));
            request.getRequestDispatcher(Views.PUBLIC_RESET_PASSWORD).forward(request, response);
            return;
        }

        String email = PasswordResetManager.getPendingEmail(request);
        CredentialStore.updatePassword(getServletContext(), email, sanitizedNewPassword);
        PasswordResetManager.clear(request);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                MessageResolver.getMessage(request, "flash.reset.success"));
        LOGGER.info("Contraseña restablecida para {}", email);
        response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
    }

    private void copyFlashMessages(HttpServletRequest request) {
        Object success = SessionManager.getAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        if (success != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_SUCCESS, success);
            SessionManager.removeAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        }

        Object error = SessionManager.getAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        if (error != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, error);
            SessionManager.removeAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        }

        Object info = SessionManager.getAttribute(request, AppConstants.ATTR_FLASH_INFO);
        if (info != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_INFO, info);
            SessionManager.removeAttribute(request, AppConstants.ATTR_FLASH_INFO);
        }
    }
}
