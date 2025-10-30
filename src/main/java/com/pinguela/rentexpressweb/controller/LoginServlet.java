package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.MailService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.MailServiceImpl;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.security.CredentialStore;
import com.pinguela.rentexpressweb.security.RememberMeManager;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.security.TwoFactorManager;
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
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/app/auth/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(LoginServlet.class);
    private static final String ATTR_ALREADY_AUTHENTICATED = "alreadyAuthenticated";
    private static final String ERROR_KEY_EMAIL = UserConstants.PARAM_EMAIL;
    private static final String ERROR_KEY_PASSWORD = UserConstants.PARAM_PASSWORD;
    private static final String ERROR_KEY_GLOBAL = "global";
    private static final String BUNDLE_BASE_NAME = "i18n.Messages";
    private static final String MESSAGE_KEY_EMAIL_REQUIRED = "login.error.requiredEmail";
    private static final String MESSAGE_KEY_PASSWORD_REQUIRED = "login.error.requiredPassword";
    private static final String MESSAGE_KEY_INVALID_CREDENTIALS = "login.error.invalidCredentials";
    private static final String MESSAGE_KEY_PAGE_TITLE = "login.title";
    private static final String MESSAGE_KEY_EMAIL_FAILURE = "error.login.2fa.email";
    private static final String MESSAGE_KEY_MAIL_SUBJECT = "mail.2fa.subject";
    private static final String MESSAGE_KEY_MAIL_BODY = "mail.2fa.body";

    private final MailService mailService = new MailServiceImpl();
    private final UserService userService = new UserServiceImpl();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RememberMeManager.applyRememberedUser(request);
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser instanceof String) {
            String normalized = ((String) currentUser).trim();
            if (normalized.isEmpty() || !CredentialStore.isKnownEmail(getServletContext(), normalized)) {
                SessionManager.removeAttribute(request, AppConstants.ATTR_CURRENT_USER);
                SessionManager.removeAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
                currentUser = null;
            }
        }

        if (TwoFactorManager.hasPendingVerification(request)) {
            response.sendRedirect(request.getContextPath() + "/app/auth/verify-2fa");
            return;
        }

        copyFlashMessages(request);
        if (currentUser != null) {
            request.setAttribute(ATTR_ALREADY_AUTHENTICATED, Boolean.TRUE);
            if (request.getAttribute(AppConstants.ATTR_REMEMBERED_EMAIL) == null) {
                request.setAttribute(AppConstants.ATTR_REMEMBERED_EMAIL, currentUser.toString());
            }
        }
        ResourceBundle bundle = resolveBundle(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, resolveMessage(bundle, MESSAGE_KEY_PAGE_TITLE));

        String rememberedEmail = RememberMeManager.resolveRememberedUser(request);
        if (rememberedEmail != null) {
            request.setAttribute(AppConstants.ATTR_REMEMBERED_EMAIL, rememberedEmail);
        }

        request.getRequestDispatcher(Views.PUBLIC_LOGIN).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter(UserConstants.PARAM_EMAIL);
        String password = request.getParameter(UserConstants.PARAM_PASSWORD);
        boolean remember = request.getParameter(UserConstants.PARAM_REMEMBER_ME) != null;

        ResourceBundle bundle = resolveBundle(request);
        Map<String, String> errors = new LinkedHashMap<String, String>();
        String sanitizedEmail = email != null ? email.trim() : null;
        if (sanitizedEmail == null || sanitizedEmail.isEmpty()) {
            errors.put(ERROR_KEY_EMAIL, resolveMessage(bundle, MESSAGE_KEY_EMAIL_REQUIRED));
        }
        if (password == null || password.trim().isEmpty()) {
            errors.put(ERROR_KEY_PASSWORD, resolveMessage(bundle, MESSAGE_KEY_PASSWORD_REQUIRED));
        }

        UserDTO authenticatedUser = null;
        if (errors.isEmpty()) {
            authenticatedUser = authenticate(sanitizedEmail, password);
        }
        if (errors.isEmpty() && authenticatedUser == null) {
            errors.put(ERROR_KEY_GLOBAL, resolveMessage(bundle, MESSAGE_KEY_INVALID_CREDENTIALS));
        }

        if (!errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, resolveMessage(bundle, MESSAGE_KEY_PAGE_TITLE));
            request.setAttribute(AppConstants.ATTR_REMEMBERED_EMAIL, sanitizedEmail);
            request.getRequestDispatcher(Views.PUBLIC_LOGIN).forward(request, response);
            return;
        }

        String loginEmail = resolveLoginEmail(authenticatedUser, sanitizedEmail);
        if (loginEmail != null) {
            CredentialStore.updatePassword(getServletContext(), loginEmail, password);
        } else if (sanitizedEmail != null) {
            CredentialStore.updatePassword(getServletContext(), sanitizedEmail, password);
        }
        String normalizedEmail = loginEmail != null ? loginEmail.toLowerCase(Locale.ROOT) : null;

        RememberMeManager.forgetUser(request, response);
        SessionManager.removeAttribute(request, AppConstants.ATTR_CURRENT_USER);
        SessionManager.removeAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);

        String verificationCode = TwoFactorManager.initiate(request, normalizedEmail, remember);
        boolean emailSent = sendVerificationCodeByEmail(request, loginEmail, verificationCode);
        if (emailSent) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_INFO,
                    buildVerificationInfoMessage(request, loginEmail, false));
            LOGGER.info("Generado código 2FA para {}", normalizedEmail);
            response.sendRedirect(request.getContextPath() + "/app/auth/verify-2fa");
        } else {
            TwoFactorManager.clear(request);
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, MESSAGE_KEY_EMAIL_FAILURE));
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
        }
    }

    private UserDTO authenticate(String identifier, String password) {
        if (identifier == null || password == null) {
            return null;
        }
        String trimmedIdentifier = identifier.trim();
        if (trimmedIdentifier.isEmpty()) {
            return null;
        }
        String normalizedIdentifier = trimmedIdentifier.toLowerCase(Locale.ROOT);
        try {
            UserDTO user = userService.authenticate(trimmedIdentifier, password);
            if (user == null) {
                LOGGER.warn("Intento de acceso fallido para {}", normalizedIdentifier);
                return null;
            }
            if (Boolean.FALSE.equals(user.getActiveStatus())) {
                LOGGER.warn("Intento de acceso para usuario inactivo {}", normalizedIdentifier);
                return null;
            }
            rememberResolvedIdentifiers(user, normalizedIdentifier);
            return user;
        } catch (RentexpresException ex) {
            LOGGER.error("Error autenticando al usuario {}", normalizedIdentifier, ex);
            return null;
        }
    }

    private void rememberResolvedIdentifiers(UserDTO user, String identifier) {
        if (identifier != null) {
            CredentialStore.rememberEmail(getServletContext(), identifier);
        }
        if (user != null && user.getEmail() != null) {
            CredentialStore.rememberEmail(getServletContext(), user.getEmail());
        }
        if (user != null && user.getUsername() != null) {
            CredentialStore.rememberEmail(getServletContext(), user.getUsername());
        }
    }

    private String resolveLoginEmail(UserDTO user, String providedIdentifier) {
        if (user != null && user.getEmail() != null) {
            String email = user.getEmail().trim();
            if (!email.isEmpty()) {
                return email;
            }
        }
        if (providedIdentifier != null) {
            String sanitized = providedIdentifier.trim();
            if (!sanitized.isEmpty()) {
                return sanitized;
            }
        }
        return null;
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
                LOGGER.error("No se pudo enviar el correo 2FA a {}", email);
                return false;
            }
            return true;
        } catch (RuntimeException ex) {
            LOGGER.error("Error inesperado enviando el correo 2FA a {}", email, ex);
            return false;
        }
    }

    private ResourceBundle resolveBundle(HttpServletRequest request) {
        Locale locale = request.getLocale();
        Object configuredLocale = SessionManager.getAttribute(request, AppConstants.ATTR_LOCALE);
        if (configuredLocale instanceof String) {
            String language = ((String) configuredLocale).trim();
            if (!language.isEmpty()) {
                locale = new Locale(language);
            }
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
    }

    private String resolveMessage(ResourceBundle bundle, String key) {
        if (bundle == null || key == null) {
            return "";
        }
        try {
            return bundle.getString(key);
        } catch (MissingResourceException ex) {
            LOGGER.warn("No se encontró el mensaje {} en el bundle {}", key, BUNDLE_BASE_NAME);
            return "";
        }
    }
}
