package com.pinguela.rentexpressweb.filter;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.EmployeeSessionResolver;
import com.pinguela.rentexpressweb.security.LoginAuthenticator;
import com.pinguela.rentexpressweb.security.LoginRequest;
import com.pinguela.rentexpressweb.security.LoginRequestValidator;
import com.pinguela.rentexpressweb.util.FlashMessageUtils;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionUtils;
import com.pinguela.rentexpressweb.util.UserActivityTracker;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controlador de autenticación sencillo que gestiona login y logout.
 */
@WebServlet(urlPatterns = { SecurityConstants.LOGIN_ENDPOINT, "/app/auth/logout" })
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(AuthServlet.class);
    private static final String ERROR_KEY_GLOBAL = "global";
    private static final String MESSAGE_KEY_INVALID_CREDENTIALS = "login.error.invalidCredentials";
    private static final String MESSAGE_KEY_LOGOUT = "flash.logout.success";
    private static final String MESSAGE_KEY_PAGE_TITLE = "login.title";
    private static final String MESSAGE_KEY_LOGIN_SUCCESS = "flash.login.success";

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isLogoutRequest(request)) {
            handleLogout(request, response);
            return;
        }
        renderLogin(request, response);
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

    private void renderLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FlashMessageUtils.transferToRequest(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                MessageResolver.getMessage(request, MESSAGE_KEY_PAGE_TITLE));
        request.getRequestDispatcher(Views.PUBLIC_LOGIN).forward(request, response);
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
            renderLogin(request, response);
            return;
        }

        String resolvedEmail = LoginAuthenticator.resolveLoginEmail(authenticatedUser, loginRequest.getEmail());
        if (resolvedEmail == null) {
            resolvedEmail = loginRequest.getEmail();
        }
        LoginAuthenticator.updateCredential(getServletContext(), resolvedEmail, loginRequest.getEmail(),
                loginRequest.getPassword());

        if (resolvedEmail != null) {
            SessionUtils.setAttribute(request, AppConstants.ATTR_CURRENT_USER, resolvedEmail);
            EmployeeSessionResolver.resolveFromEmail(request, resolvedEmail);
        } else {
            SessionUtils.removeAttribute(request, AppConstants.ATTR_CURRENT_USER);
            SessionUtils.removeAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        }

        UserActivityTracker.record(request, "home.dashboard.activity.login", "bi bi-box-arrow-in-right",
                request.getRemoteAddr());

        SessionUtils.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                MessageResolver.getMessage(request, MESSAGE_KEY_LOGIN_SUCCESS));
        LOGGER.info("Inicio de sesión correcto para {}", resolvedEmail);
        response.sendRedirect(request.getContextPath() + SecurityConstants.HOME_ENDPOINT);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object currentUser = SessionUtils.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser != null) {
            LOGGER.info("Cierre de sesión para {}", currentUser);
        }
        SessionUtils.logout(request);
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute(AppConstants.ATTR_FLASH_SUCCESS,
                MessageResolver.getMessage(request, MESSAGE_KEY_LOGOUT));
        response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        return "/app/auth/logout".equals(request.getServletPath());
    }
}
