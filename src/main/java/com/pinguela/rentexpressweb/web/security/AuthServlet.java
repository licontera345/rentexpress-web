package com.pinguela.rentexpressweb.web.security;

import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.MailService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.MailServiceImpl;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.LoginAuthenticator;
import com.pinguela.rentexpressweb.security.LoginRequest;
import com.pinguela.rentexpressweb.security.LoginRequestValidator;
import com.pinguela.rentexpressweb.security.RememberMeManager;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.security.TwoFactorManager;
import com.pinguela.rentexpressweb.util.MessageResolver;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controlador de autenticación que centraliza login, logout y recordatorio de
 * credenciales.
 */
@WebServlet(urlPatterns = { SecurityConstants.LOGIN_ENDPOINT, "/app/auth/logout" })
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(AuthServlet.class);
    private static final String ERROR_KEY_GLOBAL = "global";
    private static final String MESSAGE_KEY_INVALID_CREDENTIALS = "login.error.invalidCredentials";
    private static final String MESSAGE_KEY_EMAIL_FAILURE = "error.login.2fa.email";
    private static final String MESSAGE_KEY_LOGOUT = "flash.logout.success";

    private final MailService mailService = new MailServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isLogoutRequest(request)) {
            handleLogout(request, response);
            return;
        }
        if (TwoFactorManager.hasPendingVerification(request)) {
            response.sendRedirect(request.getContextPath() + "/app/auth/verify-2fa");
            return;
        }
        AuthViewHelper.renderLogin(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isLogoutRequest(request)) {
            handleLogout(request, response);
            return;
        }
        processLogin(request, response);
    }

    private void processLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LoginRequest loginRequest = LoginRequestValidator.validate(request);
        UserDTO authenticatedUser = null;
        if (loginRequest.getErrors().isEmpty()) {
            authenticatedUser = LoginAuthenticator.authenticate(getServletContext(), userService::authenticate,
                    loginRequest.getEmail(), loginRequest.getPassword());
            if (authenticatedUser == null) {
                loginRequest.getErrors().put(ERROR_KEY_GLOBAL,
                        MessageResolver.getMessage(request, MESSAGE_KEY_INVALID_CREDENTIALS));
            }
        }

        if (!loginRequest.getErrors().isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, loginRequest.getErrors());
            request.setAttribute(AppConstants.ATTR_REMEMBERED_EMAIL, loginRequest.getEmail());
            AuthViewHelper.renderLogin(request, response);
            return;
        }

        String resolvedEmail = LoginAuthenticator.resolveLoginEmail(authenticatedUser, loginRequest.getEmail());
        LoginAuthenticator.updateCredential(getServletContext(), resolvedEmail, loginRequest.getEmail(),
                loginRequest.getPassword());
        RememberMeManager.forgetUser(request, response);
        SessionManager.removeAttribute(request, AppConstants.ATTR_CURRENT_USER);
        SessionManager.removeAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);

        String normalizedEmail = LoginAuthenticator.normalize(resolvedEmail);
        String verificationCode = TwoFactorManager.initiate(request, normalizedEmail, loginRequest.isRemember());
        if (AuthMailHelper.sendVerification(mailService, request, resolvedEmail, verificationCode, LOGGER)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_INFO,
                    AuthMailHelper.buildVerificationInfo(request, resolvedEmail, false));
            LOGGER.info("Generado código 2FA para {}", normalizedEmail);
            response.sendRedirect(request.getContextPath() + "/app/auth/verify-2fa");
            return;
        }

        TwoFactorManager.clear(request);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                MessageResolver.getMessage(request, MESSAGE_KEY_EMAIL_FAILURE));
        response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AuthSessionHandler.logout(request, response, LOGGER,
                MessageResolver.getMessage(request, MESSAGE_KEY_LOGOUT));
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        return "/app/auth/logout".equals(request.getServletPath());
    }
}
