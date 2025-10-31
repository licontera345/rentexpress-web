package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.security.CredentialStore;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Centraliza el flujo de recuperación de contraseña (solicitud, verificación y restablecimiento).
 */
@WebServlet({ ForgotPasswordServlet.FORGOT_PATH, ForgotPasswordServlet.VERIFY_PATH,
        ForgotPasswordServlet.RESET_PATH })
public class ForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    static final String FORGOT_PATH = "/app/password/forgot";
    static final String VERIFY_PATH = "/app/password/verify-reset";
    static final String RESET_PATH = "/app/password/reset";

    private static final String PARAM_RESEND = "resend";
    private static final String PARAM_RESET_CODE = "code";
    private static final String PARAM_NEW_PASSWORD = "newPassword";
    private static final String PARAM_CONFIRM_PASSWORD = "confirmPassword";

    private static final String ATTR_FORGOT_ERRORS = "forgotPasswordErrors";
    private static final String ATTR_FORGOT_EMAIL = "forgotPasswordEmail";
    private static final String ATTR_VERIFY_ERRORS = "verifyResetErrors";
    private static final String ATTR_SUBMITTED_CODE = "submittedResetCode";
    private static final String ATTR_RESET_ERRORS = "resetPasswordErrors";
    private static final String ATTR_PENDING_EMAIL = "pendingResetEmail";
    private static final String ATTR_SECONDS_REMAINING = "resetSecondsRemaining";

    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final int VERIFICATION_CODE_VALIDITY_SECONDS = 60;
    private static final String LOGIN_PATH = "/login";

    private static final String SESSION_RESET_EMAIL = "passwordResetEmail";
    private static final String SESSION_RESET_CODE = "passwordResetCode";
    private static final String SESSION_RESET_EXPIRATION = "passwordResetExpiration";
    private static final String SESSION_RESET_VERIFIED = "passwordResetVerified";

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final Logger LOGGER = LogManager.getLogger(ForgotPasswordServlet.class);

    public ForgotPasswordServlet() {
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
        if (request.getAttribute(ATTR_FORGOT_EMAIL) == null && hasPendingReset(request)) {
            request.setAttribute(ATTR_FORGOT_EMAIL, getPendingEmail(request));
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
            request.setAttribute(ATTR_FORGOT_ERRORS, errors);
            request.setAttribute(ATTR_FORGOT_EMAIL,
                    emailParam != null ? emailParam.trim() : "");
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                    MessageResolver.getMessage(request, "page.forgotPassword.title"));
            request.getRequestDispatcher(Views.PUBLIC_FORGOT_PASSWORD).forward(request, response);
            return;
        }

        String code = startReset(request, sanitizedEmail);
        SessionManager.set(request, AppConstants.ATTR_FLASH_INFO,
                buildInfoMessage(request, sanitizedEmail, code, false));
        LOGGER.info("Generado código de restablecimiento {} para {}", code, sanitizedEmail);

        response.sendRedirect(request.getContextPath() + VERIFY_PATH);
    }

    private void handleVerifyGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!hasPendingReset(request)) {
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.verifyReset.none"));
            response.sendRedirect(request.getContextPath() + FORGOT_PATH);
            return;
        }

        if (request.getParameter(PARAM_RESEND) != null) {
            String code = regenerateCode(request);
            String email = getPendingEmail(request);
            if (code != null && email != null) {
                SessionManager.set(request, AppConstants.ATTR_FLASH_INFO,
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
        if (!hasPendingReset(request)) {
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.reset.sessionExpired"));
            response.sendRedirect(request.getContextPath() + FORGOT_PATH);
            return;
        }

        String code = request.getParameter(PARAM_RESET_CODE);
        String sanitizedCode = code != null ? code.trim() : "";

        Map<String, String> errors = new LinkedHashMap<String, String>();
        if (sanitizedCode.isEmpty()) {
            errors.put(PARAM_RESET_CODE,
                    MessageResolver.getMessage(request, "validation.verifyReset.code.required"));
        } else if (!sanitizedCode.matches("\\d{" + VERIFICATION_CODE_LENGTH + "}")) {
            errors.put(PARAM_RESET_CODE,
                    MessageResolver.getMessage(request, "validation.verifyReset.code.length",
                            Integer.valueOf(VERIFICATION_CODE_LENGTH)));
        }

        if (errors.isEmpty() && isResetExpired(request)) {
            errors.put("global" + errors.size(),
                    MessageResolver.getMessage(request, "validation.verifyReset.code.expired"));
        }

        if (errors.isEmpty() && !matchesResetCode(request, sanitizedCode)) {
            errors.put("global" + errors.size(),
                    MessageResolver.getMessage(request, "validation.verifyReset.code.invalid"));
        }

        if (!errors.isEmpty()) {
            request.setAttribute(ATTR_VERIFY_ERRORS, errors);
            request.setAttribute(ATTR_SUBMITTED_CODE, sanitizedCode);
            exposeVerificationContext(request);
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                    MessageResolver.getMessage(request, "page.verifyReset.title"));
            request.getRequestDispatcher(Views.PUBLIC_VERIFY_RESET).forward(request, response);
            return;
        }

        markResetVerified(request);
        SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                MessageResolver.getMessage(request, "flash.verifyReset.success"));
        LOGGER.info("Verificación de restablecimiento completada para {}",
                getPendingEmail(request));
        response.sendRedirect(request.getContextPath() + RESET_PATH);
    }

    private void handleResetGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!canReset(request)) {
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.reset.invalidSession"));
            response.sendRedirect(request.getContextPath() + FORGOT_PATH);
            return;
        }

        copyFlashMessages(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                MessageResolver.getMessage(request, "page.resetPassword.title"));
        request.setAttribute(ATTR_PENDING_EMAIL, getPendingEmail(request));
        request.getRequestDispatcher(Views.PUBLIC_RESET_PASSWORD).forward(request, response);
    }

    private void handleResetPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!canReset(request)) {
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.reset.sessionExpired"));
            response.sendRedirect(request.getContextPath() + FORGOT_PATH);
            return;
        }

        String newPassword = request.getParameter(PARAM_NEW_PASSWORD);
        String confirmPassword = request.getParameter(PARAM_CONFIRM_PASSWORD);
        String sanitizedNewPassword = newPassword != null ? newPassword.trim() : null;
        String sanitizedConfirm = confirmPassword != null ? confirmPassword.trim() : null;

        Map<String, String> errors = new LinkedHashMap<String, String>();
        if (sanitizedNewPassword == null || sanitizedNewPassword.isEmpty()) {
            errors.put(PARAM_NEW_PASSWORD,
                    MessageResolver.getMessage(request, "validation.reset.newPassword.required"));
        } else if (sanitizedNewPassword.length() < 8) {
            errors.put(PARAM_NEW_PASSWORD,
                    MessageResolver.getMessage(request, "validation.reset.newPassword.length"));
        }

        if (sanitizedConfirm == null || sanitizedConfirm.isEmpty()) {
            errors.put(PARAM_CONFIRM_PASSWORD,
                    MessageResolver.getMessage(request, "validation.reset.confirm.required"));
        } else if (sanitizedNewPassword != null && !sanitizedConfirm.equals(sanitizedNewPassword)) {
            errors.put(PARAM_CONFIRM_PASSWORD,
                    MessageResolver.getMessage(request, "validation.reset.confirm.mismatch"));
        }

        if (!errors.isEmpty()) {
            request.setAttribute(ATTR_RESET_ERRORS, errors);
            request.setAttribute(ATTR_PENDING_EMAIL, getPendingEmail(request));
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                    MessageResolver.getMessage(request, "page.resetPassword.title"));
            request.getRequestDispatcher(Views.PUBLIC_RESET_PASSWORD).forward(request, response);
            return;
        }

        String email = getPendingEmail(request);
        CredentialStore.updatePassword(getServletContext(), email, sanitizedNewPassword);
        clearResetContext(request);
        SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                MessageResolver.getMessage(request, "flash.reset.success"));
        LOGGER.info("Contraseña restablecida para {}", email);
        response.sendRedirect(request.getContextPath() + LOGIN_PATH);
    }

    private void copyFlashMessages(HttpServletRequest request) {
        Object success = SessionManager.get(request, AppConstants.ATTR_FLASH_SUCCESS);
        if (success != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_SUCCESS, success);
            SessionManager.remove(request, AppConstants.ATTR_FLASH_SUCCESS);
        }

        Object error = SessionManager.get(request, AppConstants.ATTR_FLASH_ERROR);
        if (error != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, error);
            SessionManager.remove(request, AppConstants.ATTR_FLASH_ERROR);
        }

        Object info = SessionManager.get(request, AppConstants.ATTR_FLASH_INFO);
        if (info != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_INFO, info);
            SessionManager.remove(request, AppConstants.ATTR_FLASH_INFO);
        }
    }

    private void exposeVerificationContext(HttpServletRequest request) {
        request.setAttribute(ATTR_PENDING_EMAIL, getPendingEmail(request));
        request.setAttribute(ATTR_SECONDS_REMAINING,
                Long.valueOf(secondsUntilExpiration(request)));
    }

    private String buildInfoMessage(HttpServletRequest request, String email, String code, boolean resent) {
        String key = resent ? "info.reset.code.resent" : "info.reset.code.sent";
        return MessageResolver.getMessage(request, key, email, code,
                Integer.valueOf(VERIFICATION_CODE_VALIDITY_SECONDS));
    }

    private String startReset(HttpServletRequest request, String email) {
        String code = generateCode();
        long expiration = System.currentTimeMillis() + (VERIFICATION_CODE_VALIDITY_SECONDS * 1000L);
        SessionManager.set(request, SESSION_RESET_EMAIL, email);
        SessionManager.set(request, SESSION_RESET_CODE, code);
        SessionManager.set(request, SESSION_RESET_EXPIRATION, Long.valueOf(expiration));
        SessionManager.set(request, SESSION_RESET_VERIFIED, Boolean.FALSE);
        return code;
    }

    private boolean hasPendingReset(HttpServletRequest request) {
        return SessionManager.get(request, SESSION_RESET_EMAIL) != null
                && SessionManager.get(request, SESSION_RESET_CODE) != null;
    }

    private String regenerateCode(HttpServletRequest request) {
        if (!hasPendingReset(request)) {
            return null;
        }
        String email = getPendingEmail(request);
        if (email == null) {
            return null;
        }
        return startReset(request, email);
    }

    private String getPendingEmail(HttpServletRequest request) {
        Object value = SessionManager.get(request, SESSION_RESET_EMAIL);
        return value != null ? value.toString() : null;
    }

    private boolean isResetExpired(HttpServletRequest request) {
        Long expiration = resolveExpirationMillis(request);
        if (expiration == null) {
            return true;
        }
        return System.currentTimeMillis() > expiration.longValue();
    }

    private long secondsUntilExpiration(HttpServletRequest request) {
        Long expiration = resolveExpirationMillis(request);
        if (expiration == null) {
            return 0L;
        }
        long remaining = expiration.longValue() - System.currentTimeMillis();
        if (remaining <= 0L) {
            return 0L;
        }
        return remaining / 1000L;
    }

    private boolean matchesResetCode(HttpServletRequest request, String providedCode) {
        Object expected = SessionManager.get(request, SESSION_RESET_CODE);
        return expected != null && expected.toString().equals(providedCode);
    }

    private void markResetVerified(HttpServletRequest request) {
        SessionManager.set(request, SESSION_RESET_VERIFIED, Boolean.TRUE);
    }

    private boolean canReset(HttpServletRequest request) {
        Object verified = SessionManager.get(request, SESSION_RESET_VERIFIED);
        return Boolean.TRUE.equals(verified) && getPendingEmail(request) != null;
    }

    private void clearResetContext(HttpServletRequest request) {
        SessionManager.remove(request, SESSION_RESET_EMAIL);
        SessionManager.remove(request, SESSION_RESET_CODE);
        SessionManager.remove(request, SESSION_RESET_EXPIRATION);
        SessionManager.remove(request, SESSION_RESET_VERIFIED);
    }

    private Long resolveExpirationMillis(HttpServletRequest request) {
        Object value = SessionManager.get(request, SESSION_RESET_EXPIRATION);
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof String) {
            try {
                return Long.valueOf(Long.parseLong((String) value));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private String generateCode() {
        int bound = (int) Math.pow(10, VERIFICATION_CODE_LENGTH);
        int number = RANDOM.nextInt(bound);
        return String.format("%0" + VERIFICATION_CODE_LENGTH + "d", Integer.valueOf(number));
    }
}
