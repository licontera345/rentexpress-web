package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.PasswordConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
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
 * Servlet implementation class VerifyResetServlet
 */
@WebServlet("/app/password/verify-reset")
public class VerifyResetServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(VerifyResetServlet.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerifyResetServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PasswordResetManager.hasPending(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "No hay ninguna solicitud de restablecimiento activa.");
            response.sendRedirect(request.getContextPath() + "/app/password/forgot");
            return;
        }

        if (request.getParameter(SecurityConstants.PARAM_RESEND) != null) {
            String code = PasswordResetManager.regenerate(request);
            String email = PasswordResetManager.getPendingEmail(request);
            if (code != null && email != null) {
                SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_INFO,
                        buildInfoMessage(email, code, true));
                LOGGER.info("Reenviado código de restablecimiento {} para {}", code, email);
            }
            response.sendRedirect(request.getContextPath() + "/app/password/verify-reset");
            return;
        }

        copyFlashMessages(request);
        exposeVerificationContext(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Verifica tu código");
        request.getRequestDispatcher(Views.PUBLIC_VERIFY_RESET).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PasswordResetManager.hasPending(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "La sesión de restablecimiento ha caducado. Solicita un nuevo código.");
            response.sendRedirect(request.getContextPath() + "/app/password/forgot");
            return;
        }

        String code = request.getParameter(PasswordConstants.PARAM_RESET_CODE);
        String sanitizedCode = code != null ? code.trim() : "";

        List<String> errors = new ArrayList<>();
        if (sanitizedCode.isEmpty()) {
            errors.add("Debes introducir el código de verificación.");
        } else if (!sanitizedCode.matches("\\d{" + SecurityConstants.TWO_FA_CODE_LENGTH + "}")) {
            errors.add("El código debe tener " + SecurityConstants.TWO_FA_CODE_LENGTH + " dígitos.");
        }

        if (errors.isEmpty() && PasswordResetManager.isExpired(request)) {
            errors.add("El código ha caducado. Solicita uno nuevo.");
        }

        if (errors.isEmpty() && !PasswordResetManager.matchesCode(request, sanitizedCode)) {
            errors.add("El código introducido no es válido.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute(PasswordConstants.ATTR_VERIFY_ERRORS, errors);
            request.setAttribute(PasswordConstants.ATTR_SUBMITTED_CODE, sanitizedCode);
            exposeVerificationContext(request);
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Verifica tu código");
            request.getRequestDispatcher(Views.PUBLIC_VERIFY_RESET).forward(request, response);
            return;
        }

        PasswordResetManager.markVerified(request);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                "Código validado. Ahora puedes definir una nueva contraseña.");
        LOGGER.info("Verificación de restablecimiento completada para {}",
                PasswordResetManager.getPendingEmail(request));
        response.sendRedirect(request.getContextPath() + "/app/password/reset");
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

    private void exposeVerificationContext(HttpServletRequest request) {
        request.setAttribute(PasswordConstants.ATTR_PENDING_EMAIL, PasswordResetManager.getPendingEmail(request));
        request.setAttribute(PasswordConstants.ATTR_SECONDS_REMAINING,
                Long.valueOf(PasswordResetManager.secondsUntilExpiration(request)));
    }

    private String buildInfoMessage(String email, String code, boolean resent) {
        String intro = resent ? "Hemos reenviado un nuevo código" : "Hemos enviado un código temporal";
        return String.format("%s a %s. Por tratarse de un entorno académico, el código es %s y caduca en %d segundos.",
                intro,
                email,
                code,
                Integer.valueOf(SecurityConstants.TWO_FA_CODE_VALIDITY_SECONDS));
    }
}
