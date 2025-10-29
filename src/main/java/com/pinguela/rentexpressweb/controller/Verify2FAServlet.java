package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.EmployeeSessionResolver;
import com.pinguela.rentexpressweb.security.RememberMeManager;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.security.TwoFactorManager;

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
 * Gestiona la verificación de códigos 2FA tras un login correcto.
 */
@WebServlet("/app/auth/verify-2fa")
public class Verify2FAServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(Verify2FAServlet.class);

    public Verify2FAServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!TwoFactorManager.hasPendingVerification(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "Tu sesión de verificación ha caducado. Inicia sesión de nuevo.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }

        if (request.getParameter(SecurityConstants.PARAM_RESEND) != null) {
            String code = TwoFactorManager.regenerate(request);
            String email = TwoFactorManager.getPendingEmail(request);
            if (code != null && email != null) {
                SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_INFO,
                        buildVerificationInfoMessage(email, code, true));
                LOGGER.info("Reenviado código 2FA {} para {}", code, email);
            }
            response.sendRedirect(request.getContextPath() + "/app/auth/verify-2fa");
            return;
        }

        copyFlashMessages(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Verificación en dos pasos");
        request.setAttribute("pendingEmail", TwoFactorManager.getPendingEmail(request));
        request.setAttribute("secondsRemaining", Long.valueOf(TwoFactorManager.secondsUntilExpiration(request)));
        request.getRequestDispatcher("/private/security/verify_2fa.jsp").forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!TwoFactorManager.hasPendingVerification(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "Tu sesión de verificación ha caducado. Inicia sesión de nuevo.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }

        String code = request.getParameter(SecurityConstants.PARAM_2FA_CODE);
        String sanitizedCode = code != null ? code.trim() : "";

        List<String> errors = new ArrayList<>();
        if (sanitizedCode.isEmpty()) {
            errors.add("Debes introducir el código de verificación.");
        } else if (!sanitizedCode.matches("\\d{" + SecurityConstants.TWO_FA_CODE_LENGTH + "}")) {
            errors.add("El código debe tener " + SecurityConstants.TWO_FA_CODE_LENGTH + " dígitos.");
        }

        if (errors.isEmpty() && TwoFactorManager.isExpired(request)) {
            errors.add("El código ha caducado. Solicita uno nuevo.");
        }

        if (errors.isEmpty() && !TwoFactorManager.matchesCode(request, sanitizedCode)) {
            errors.add("El código introducido no es válido.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("submittedCode", sanitizedCode);
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Verificación en dos pasos");
            request.setAttribute("pendingEmail", TwoFactorManager.getPendingEmail(request));
            request.setAttribute("secondsRemaining", Long.valueOf(TwoFactorManager.secondsUntilExpiration(request)));
            request.getRequestDispatcher("/private/security/verify_2fa.jsp").forward(request, response);
            return;
        }

        String email = TwoFactorManager.getPendingEmail(request);
        SessionManager.setAttribute(request, AppConstants.ATTR_CURRENT_USER, email);
        EmployeeSessionResolver.resolveFromEmail(request, email);
        if (TwoFactorManager.shouldRemember(request)) {
            RememberMeManager.rememberUser(response, email);
        } else {
            RememberMeManager.forgetUser(response);
        }

        TwoFactorManager.clear(request);
        LOGGER.info("Verificación 2FA completada para {}", email);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                "¡Bienvenido de nuevo! Has iniciado sesión correctamente.");
        response.sendRedirect(request.getContextPath() + SecurityConstants.HOME_ENDPOINT);
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

    private String buildVerificationInfoMessage(String email, String code, boolean resent) {
        String intro = resent ? "Hemos reenviado un nuevo código" : "Hemos enviado un código";
        return String.format(
                "%s a %s. Por tratarse de un entorno académico, el código es %s y caduca en %d segundos.",
                intro, email, code, SecurityConstants.TWO_FA_CODE_VALIDITY_SECONDS);
    }
}
