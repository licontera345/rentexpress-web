package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.security.CredentialStore;
import com.pinguela.rentexpressweb.security.RememberMeManager;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.security.TwoFactorManager;
import com.pinguela.rentexpressweb.util.PasswordEncoder;
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
 * Servlet implementation class LoginServlet
 */
@WebServlet("/app/auth/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(LoginServlet.class);

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
        if (currentUser != null) {
            response.sendRedirect(request.getContextPath() + SecurityConstants.HOME_ENDPOINT);
            return;
        }

        if (TwoFactorManager.hasPendingVerification(request)) {
            response.sendRedirect(request.getContextPath() + "/app/auth/verify-2fa");
            return;
        }

        copyFlashMessages(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Inicia sesión");

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

        List<String> errors = new ArrayList<>();
        if (email == null || email.trim().isEmpty()) {
            errors.add("El correo electrónico es obligatorio.");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.add("La contraseña es obligatoria.");
        }

        if (errors.isEmpty() && !authenticate(email, password)) {
            errors.add("Credenciales no válidas. Revisa tu correo y contraseña.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Inicia sesión");
            request.setAttribute(AppConstants.ATTR_REMEMBERED_EMAIL, email);
            request.getRequestDispatcher(Views.PUBLIC_LOGIN).forward(request, response);
            return;
        }

        String sanitizedEmail = email != null ? email.trim() : null;
        RememberMeManager.forgetUser(response);
        SessionManager.removeAttribute(request, AppConstants.ATTR_CURRENT_USER);
        SessionManager.removeAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);

        String verificationCode = TwoFactorManager.initiate(request, sanitizedEmail, remember);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_INFO,
                buildVerificationInfoMessage(sanitizedEmail, verificationCode, false));
        LOGGER.info("Generado código 2FA {} para {}", verificationCode, sanitizedEmail);

        response.sendRedirect(request.getContextPath() + "/app/auth/verify-2fa");
    }

    private boolean authenticate(String email, String password) {
        if (email == null || password == null) {
            return false;
        }
        String sanitizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String storedHash = CredentialStore.findHashedPassword(getServletContext(), sanitizedEmail);
        if (storedHash != null && PasswordEncoder.matches(password, storedHash)) {
            return true;
        }

        return false;
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
