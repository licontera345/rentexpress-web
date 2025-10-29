package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.service.MailService;
import com.pinguela.rentexpres.service.impl.MailServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.EmployeeSessionResolver;
import com.pinguela.rentexpressweb.security.RememberMeManager;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.security.TwoFactorManager;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.UserActivityTracker;

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
 * Gestiona la verificación de códigos 2FA tras un login correcto.
 */
@WebServlet("/app/auth/verify-2fa")
public class Verify2FAServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(Verify2FAServlet.class);
    private static final String ATTR_PENDING_EMAIL = "pendingEmail";
    private static final String ATTR_SECONDS_REMAINING = "secondsRemaining";
    private static final String ATTR_SUBMITTED_CODE = "submittedCode";
    private static final String ERROR_KEY_CODE = SecurityConstants.PARAM_2FA_CODE;
    private static final String ERROR_KEY_GLOBAL = "global";
    private static final String VIEW_VERIFY_2FA = "/private/security/verify_2fa.jsp";
    private static final String MESSAGE_KEY_EMAIL_FAILURE = "error.login.2fa.email";
    private static final String MESSAGE_KEY_MAIL_SUBJECT = "mail.2fa.subject";
    private static final String MESSAGE_KEY_MAIL_BODY = "mail.2fa.body";

    private final MailService mailService = new MailServiceImpl();

    public Verify2FAServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!TwoFactorManager.hasPendingVerification(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.verify2fa.sessionExpired"));
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }

        if (request.getParameter(SecurityConstants.PARAM_RESEND) != null) {
            String code = TwoFactorManager.regenerate(request);
            String email = TwoFactorManager.getPendingEmail(request);
            if (code != null && email != null) {
                boolean emailSent = sendVerificationCodeByEmail(request, email, code);
                if (emailSent) {
                    SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_INFO,
                            buildVerificationInfoMessage(request, email, true));
                    LOGGER.info("Reenviado código 2FA para {}", email);
                } else {
                    SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                            MessageResolver.getMessage(request, MESSAGE_KEY_EMAIL_FAILURE));
                }
            }
            response.sendRedirect(request.getContextPath() + "/app/auth/verify-2fa");
            return;
        }

        copyFlashMessages(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                MessageResolver.getMessage(request, "page.verify2fa.title"));
        request.setAttribute(ATTR_PENDING_EMAIL, TwoFactorManager.getPendingEmail(request));
        request.setAttribute(ATTR_SECONDS_REMAINING, Long.valueOf(TwoFactorManager.secondsUntilExpiration(request)));
        request.getRequestDispatcher(VIEW_VERIFY_2FA).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!TwoFactorManager.hasPendingVerification(request)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.verify2fa.sessionExpired"));
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }

        String code = request.getParameter(SecurityConstants.PARAM_2FA_CODE);
        String sanitizedCode = code != null ? code.trim() : "";

        Map<String, String> errors = new LinkedHashMap<String, String>();
        if (sanitizedCode.isEmpty()) {
            errors.put(ERROR_KEY_CODE,
                    MessageResolver.getMessage(request, "validation.verify2fa.code.required"));
        } else if (!sanitizedCode.matches("\\d{" + SecurityConstants.TWO_FA_CODE_LENGTH + "}")) {
            errors.put(ERROR_KEY_CODE,
                    MessageResolver.getMessage(request, "validation.verify2fa.code.length",
                            Integer.valueOf(SecurityConstants.TWO_FA_CODE_LENGTH)));
        }

        if (errors.isEmpty() && TwoFactorManager.isExpired(request)) {
            errors.put(ERROR_KEY_GLOBAL,
                    MessageResolver.getMessage(request, "validation.verify2fa.code.expired"));
        }

        if (errors.isEmpty() && !TwoFactorManager.matchesCode(request, sanitizedCode)) {
            errors.put(ERROR_KEY_GLOBAL,
                    MessageResolver.getMessage(request, "validation.verify2fa.code.invalid"));
        }

        if (!errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            request.setAttribute(ATTR_SUBMITTED_CODE, sanitizedCode);
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                    MessageResolver.getMessage(request, "page.verify2fa.title"));
            request.setAttribute(ATTR_PENDING_EMAIL, TwoFactorManager.getPendingEmail(request));
            request.setAttribute(ATTR_SECONDS_REMAINING, Long.valueOf(TwoFactorManager.secondsUntilExpiration(request)));
            request.getRequestDispatcher(VIEW_VERIFY_2FA).forward(request, response);
            return;
        }

        String email = TwoFactorManager.getPendingEmail(request);
        SessionManager.setAttribute(request, AppConstants.ATTR_CURRENT_USER, email);
        EmployeeSessionResolver.resolveFromEmail(request, email);
        if (TwoFactorManager.shouldRemember(request)) {
            RememberMeManager.rememberUser(request, response, email);
        } else {
            RememberMeManager.forgetUser(request, response);
        }

        TwoFactorManager.clear(request);
        LOGGER.info("Verificación 2FA completada para {}", email);
        UserActivityTracker.record(request, "home.dashboard.activity.login", "bi bi-box-arrow-in-right",
                request.getRemoteAddr());
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                MessageResolver.getMessage(request, "flash.login.success"));
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

    private String buildVerificationInfoMessage(HttpServletRequest request, String email, boolean resent) {
        String key = resent ? "info.login.2fa.resent" : "info.login.2fa.sent";
        return MessageResolver.getMessage(request, key, email,
                Integer.valueOf(SecurityConstants.TWO_FA_CODE_VALIDITY_SECONDS));
    }

    private boolean sendVerificationCodeByEmail(HttpServletRequest request, String email, String code) {
        if (email == null || code == null) {
            return false;
        }
        String subject = MessageResolver.getMessage(request, MESSAGE_KEY_MAIL_SUBJECT);
        String body = MessageResolver.getMessage(request, MESSAGE_KEY_MAIL_BODY, code,
                Integer.valueOf(SecurityConstants.TWO_FA_CODE_VALIDITY_SECONDS));
        try {
            if (!mailService.send(email, subject, body)) {
                LOGGER.error("No se pudo reenviar el correo 2FA a {}", email);
                return false;
            }
            return true;
        } catch (RuntimeException ex) {
            LOGGER.error("Error inesperado reenviando el correo 2FA a {}", email, ex);
            return false;
        }
    }
}
