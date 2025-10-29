package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.PasswordConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
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
 * Servlet implementation class VerifyResetServlet
 */
@WebServlet("/app/password/verify-reset")
public class VerifyResetServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(VerifyResetServlet.class);
    private static final String ERROR_KEY_GLOBAL_PREFIX = "global";

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
                    MessageResolver.getMessage(request, "error.verifyReset.none"));
            response.sendRedirect(request.getContextPath() + "/app/password/forgot");
            return;
        }

        if (request.getParameter(SecurityConstants.PARAM_RESEND) != null) {
            String code = PasswordResetManager.regenerate(request);
            String email = PasswordResetManager.getPendingEmail(request);
            if (code != null && email != null) {
                SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_INFO,
                        buildInfoMessage(request, email, code, true));
                LOGGER.info("Reenviado código de restablecimiento {} para {}", code, email);
            }
            response.sendRedirect(request.getContextPath() + "/app/password/verify-reset");
            return;
        }

        copyFlashMessages(request);
        exposeVerificationContext(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                MessageResolver.getMessage(request, "page.verifyReset.title"));
        request.getRequestDispatcher(Views.PUBLIC_VERIFY_RESET).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!PasswordResetManager.hasPending(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.reset.sessionExpired"));
            response.sendRedirect(request.getContextPath() + "/app/password/forgot");
            return;
        }

        String code = request.getParameter(PasswordConstants.PARAM_RESET_CODE);
        String sanitizedCode = code != null ? code.trim() : "";

        Map<String, String> errors = new LinkedHashMap<String, String>();
        if (sanitizedCode.isEmpty()) {
            errors.put(PasswordConstants.PARAM_RESET_CODE,
                    MessageResolver.getMessage(request, "validation.verifyReset.code.required"));
        } else if (!sanitizedCode.matches("\\d{" + SecurityConstants.TWO_FA_CODE_LENGTH + "}")) {
            errors.put(PasswordConstants.PARAM_RESET_CODE,
                    MessageResolver.getMessage(request, "validation.verifyReset.code.length",
                            Integer.valueOf(SecurityConstants.TWO_FA_CODE_LENGTH)));
        }

        if (errors.isEmpty() && PasswordResetManager.isExpired(request)) {
            errors.put(ERROR_KEY_GLOBAL_PREFIX + errors.size(),
                    MessageResolver.getMessage(request, "validation.verifyReset.code.expired"));
        }

        if (errors.isEmpty() && !PasswordResetManager.matchesCode(request, sanitizedCode)) {
            errors.put(ERROR_KEY_GLOBAL_PREFIX + errors.size(),
                    MessageResolver.getMessage(request, "validation.verifyReset.code.invalid"));
        }

        if (!errors.isEmpty()) {
            request.setAttribute(PasswordConstants.ATTR_VERIFY_ERRORS, errors);
            request.setAttribute(PasswordConstants.ATTR_SUBMITTED_CODE, sanitizedCode);
            exposeVerificationContext(request);
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                    MessageResolver.getMessage(request, "page.verifyReset.title"));
            request.getRequestDispatcher(Views.PUBLIC_VERIFY_RESET).forward(request, response);
            return;
        }

        PasswordResetManager.markVerified(request);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                MessageResolver.getMessage(request, "flash.verifyReset.success"));
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

    private String buildInfoMessage(HttpServletRequest request, String email, String code, boolean resent) {
        String key = resent ? "info.reset.code.resent" : "info.reset.code.sent";
        return MessageResolver.getMessage(request, key, email, code,
                Integer.valueOf(SecurityConstants.TWO_FA_CODE_VALIDITY_SECONDS));
    }
}
