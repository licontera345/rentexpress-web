package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.PasswordConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
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
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class ForgotPasswordServlet
 */
@WebServlet("/app/password/forgot")
public class ForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(ForgotPasswordServlet.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ForgotPasswordServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        copyFlashMessages(request);
        if (request.getAttribute(PasswordConstants.ATTR_FORGOT_EMAIL) == null
                && PasswordResetManager.hasPending(request)) {
            request.setAttribute(PasswordConstants.ATTR_FORGOT_EMAIL, PasswordResetManager.getPendingEmail(request));
        }
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Recupera tu contraseña");
        request.getRequestDispatcher(Views.PUBLIC_FORGOT_PASSWORD).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String emailParam = request.getParameter(UserConstants.PARAM_EMAIL);
        String sanitizedEmail = emailParam != null ? emailParam.trim().toLowerCase(Locale.ROOT) : null;

        List<String> errors = new ArrayList<>();
        if (sanitizedEmail == null || sanitizedEmail.isEmpty()) {
            errors.add("El correo electrónico es obligatorio.");
        } else if (!CredentialStore.isKnownEmail(getServletContext(), sanitizedEmail)) {
            errors.add("No hemos encontrado ninguna cuenta con ese correo.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute(PasswordConstants.ATTR_FORGOT_ERRORS, errors);
            request.setAttribute(PasswordConstants.ATTR_FORGOT_EMAIL, emailParam != null ? emailParam.trim() : "");
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Recupera tu contraseña");
            request.getRequestDispatcher(Views.PUBLIC_FORGOT_PASSWORD).forward(request, response);
            return;
        }

        String code = PasswordResetManager.initiate(request, sanitizedEmail);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_INFO,
                buildInfoMessage(sanitizedEmail, code));
        LOGGER.info("Generado código de restablecimiento {} para {}", code, sanitizedEmail);

        response.sendRedirect(request.getContextPath() + "/app/password/verify-reset");
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

    private String buildInfoMessage(String email, String code) {
        return String.format(
                "Hemos enviado un código temporal a %s. Por tratarse de un entorno académico, el código es %s y caduca en %d segundos.",
                email,
                code,
                Integer.valueOf(SecurityConstants.TWO_FA_CODE_VALIDITY_SECONDS));
    }
}
