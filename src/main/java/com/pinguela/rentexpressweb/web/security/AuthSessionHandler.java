package com.pinguela.rentexpressweb.web.security;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.SessionManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

/**
 * Gestiona tareas de sesión relacionadas con la autenticación.
 */
final class AuthSessionHandler {

    private AuthSessionHandler() {
    }

    static void logout(HttpServletRequest request, HttpServletResponse response, Logger logger, String flashMessage)
            throws IOException {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser != null) {
            logger.info("Cierre de sesión para {}", currentUser);
        }
        SessionManager.logout(request);
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute(AppConstants.ATTR_FLASH_SUCCESS, flashMessage);
        response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
    }
}
