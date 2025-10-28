package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.PasswordConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.CredentialStore;
import com.pinguela.rentexpressweb.security.PasswordResetManager;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                    "No hemos podido validar el código de restablecimiento. Inicia el proceso de nuevo.");
            response.sendRedirect(request.getContextPath() + "/app/password/forgot");
            return;
        }

        copyFlashMessages(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Crea una contraseña nueva");
        request.setAttribute(PasswordConstants.ATTR_PENDING_EMAIL, PasswordResetManager.getPendingEmail(request));
        request.getRequestDispatcher(Views.PUBLIC_RESET_PASSWORD).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PasswordResetManager.canReset(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "La sesión de restablecimiento ha caducado. Solicita un nuevo código.");
            response.sendRedirect(request.getContextPath() + "/app/password/forgot");
            return;
        }

        String newPassword = request.getParameter(PasswordConstants.PARAM_NEW_PASSWORD);
        String confirmPassword = request.getParameter(PasswordConstants.PARAM_CONFIRM_PASSWORD);
        String sanitizedNewPassword = newPassword != null ? newPassword.trim() : null;
        String sanitizedConfirm = confirmPassword != null ? confirmPassword.trim() : null;

        List<String> errors = new ArrayList<>();
        if (sanitizedNewPassword == null || sanitizedNewPassword.isEmpty()) {
            errors.add("Debes indicar una nueva contraseña.");
        } else if (sanitizedNewPassword.length() < 8) {
            errors.add("La contraseña debe tener al menos 8 caracteres.");
        }

        if (sanitizedConfirm == null || sanitizedConfirm.isEmpty()) {
            errors.add("Confirma la contraseña para evitar errores.");
        } else if (sanitizedNewPassword != null && !sanitizedConfirm.equals(sanitizedNewPassword)) {
            errors.add("Las contraseñas no coinciden.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute(PasswordConstants.ATTR_RESET_ERRORS, errors);
            request.setAttribute(PasswordConstants.ATTR_PENDING_EMAIL, PasswordResetManager.getPendingEmail(request));
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Crea una contraseña nueva");
            request.getRequestDispatcher(Views.PUBLIC_RESET_PASSWORD).forward(request, response);
            return;
        }

        String email = PasswordResetManager.getPendingEmail(request);
        CredentialStore.updatePassword(getServletContext(), email, sanitizedNewPassword);
        PasswordResetManager.clear(request);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                "Contraseña restablecida. Ya puedes iniciar sesión.");
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
