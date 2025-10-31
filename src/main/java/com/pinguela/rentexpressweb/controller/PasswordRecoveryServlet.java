package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.PasswordConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
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
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Centraliza el flujo de recuperación de contraseña (solicitud, verificación y restablecimiento).
 */
@WebServlet({ PasswordRecoveryServlet.FORGOT_PATH, PasswordRecoveryServlet.VERIFY_PATH,
        PasswordRecoveryServlet.RESET_PATH })
public class PasswordRecoveryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    static final String FORGOT_PATH = "/app/password/forgot";
    static final String VERIFY_PATH = "/app/password/verify-reset";
    static final String RESET_PATH = "/app/password/reset";

    private static final Logger LOGGER = LogManager.getLogger(PasswordRecoveryServlet.class);

    public PasswordRecoveryServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (FORGOT_PATH.equals(path)) {
            handleForgotGet(request, response);
        } else if (VERIFY_PATH.equals(path)) {
            handleVerifyGet(request, response);
        } else if (RESET_PATH.equals(path)) {
            handleResetGet(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (FORGOT_PATH.equals(path)) {
            handleForgotPost(request, response);
        } else if (VERIFY_PATH.equals(path)) {
            handleVerifyPost(request, response);
        } else if (RESET_PATH.equals(path)) {
            handleResetPost(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleForgotGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        copyFlashMessages(request);
        if (request.getAttribute(PasswordConstants.ATTR_FORGOT_EMAIL) == null
                && PasswordResetManager.hasPending(request)) {
            request.setAttribute(PasswordConstants.ATTR_FORGOT_EMAIL,
                    PasswordResetManager.getPendingEmail(request));
        }
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                MessageResolver.getMessage(request, "page.forgotPassword.title"));
        request.getRequestDispatcher(Views.PUBLIC_FORGOT_PASSWORD).forward(request, response);
    }

    private void handleForgotPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String emailParam = request.getParameter(UserConstants.PARAM_EMAIL);
        String sanitizedEmail = emailParam != null ? emailParam.trim().toLowerCase(Locale.ROOT) : null;

        Map<String, String> errors = new LinkedHashMap<String, String>();
        if (sanitizedEmail == null || sanitizedEmail.isEmpty()) {
            errors.put(UserConstants.PARAM_EMAIL,
                    MessageResolver.getMessage(request, "validation.forgot.email.required"));
        } else if (!CredentialStore.isKnownEmail(getServletContext(), sanitizedEmail)) {
            errors.put(UserConstants.PARAM_EMAIL,
                    MessageResolver.getMessage(request, "validation.forgot.email.unknown"));
        }

        if (!errors.isEmpty()) {
            request.setAttribute(PasswordConstants.ATTR_FORGOT_ERRORS, errors);
            request.setAttribute(PasswordConstants.ATTR_FORGOT_EMAIL,
                    emailParam != null ? emailParam.trim() : "");
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                    MessageResolver.getMessage(request, "page.forgotPassword.title"));
            request.getRequestDispatcher(Views.PUBLIC_FORGOT_PASSWORD).forward(request, response);
            return;
        }

        String code = PasswordResetManager.initiate(request, sanitizedEmail);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_INFO,
                buildInfoMessage(request, sanitizedEmail, code, false));
        LOGGER.info("Generado código de restablecimiento {} para {}", code, sanitizedEmail);

        response.sendRedirect(request.getContextPath() + VERIFY_PATH);
    }

    private void handleVerifyGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!PasswordResetManager.hasPending(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.verifyReset.none"));
            response.sendRedirect(request.getContextPath() + FORGOT_PATH);
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
            response.sendRedirect(request.getContextPath() + VERIFY_PATH);
            return;
        }

        copyFlashMessages(request);
        exposeVerificationContext(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                MessageResolver.getMessage(request, "page.verifyReset.title"));
        request.getRequestDispatcher(Views.PUBLIC_VERIFY_RESET).forward(request, response);
    }

    private void handleVerifyPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!PasswordResetManager.hasPending(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.reset.sessionExpired"));
            response.sendRedirect(request.getContextPath() + FORGOT_PATH);
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
            errors.put("global" + errors.size(),
                    MessageResolver.getMessage(request, "validation.verifyReset.code.expired"));
        }

        if (errors.isEmpty() && !PasswordResetManager.matchesCode(request, sanitizedCode)) {
            errors.put("global" + errors.size(),
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
        response.sendRedirect(request.getContextPath() + RESET_PATH);
    }

    private void handleResetGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!PasswordResetManager.canReset(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.reset.invalidSession"));
            response.sendRedirect(request.getContextPath() + FORGOT_PATH);
            return;
        }

        copyFlashMessages(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                MessageResolver.getMessage(request, "page.resetPassword.title"));
        request.setAttribute(PasswordConstants.ATTR_PENDING_EMAIL, PasswordResetManager.getPendingEmail(request));
        request.getRequestDispatcher(Views.PUBLIC_RESET_PASSWORD).forward(request, response);
    }

    private void handleResetPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!PasswordResetManager.canReset(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.reset.sessionExpired"));
            response.sendRedirect(request.getContextPath() + FORGOT_PATH);
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
