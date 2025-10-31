package com.pinguela.rentexpressweb.web.security;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.security.RememberMeManager;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.util.FlashMessageUtils;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Ayuda a preparar y renderizar la vista de login.
 */
public final class AuthViewHelper {

    private static final String ATTR_ALREADY_AUTHENTICATED = "alreadyAuthenticated";
    private static final String MESSAGE_KEY_PAGE_TITLE = "login.title";

    private AuthViewHelper() {
    }

    public static void renderLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FlashMessageUtils.transferToRequest(request);
        applyAuthenticatedUser(request);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, MessageResolver.getMessage(request, MESSAGE_KEY_PAGE_TITLE));
        request.getRequestDispatcher(Views.PUBLIC_LOGIN).forward(request, response);
    }

    private static void applyAuthenticatedUser(HttpServletRequest request) {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser != null) {
            request.setAttribute(ATTR_ALREADY_AUTHENTICATED, Boolean.TRUE);
            if (request.getAttribute(AppConstants.ATTR_REMEMBERED_EMAIL) == null) {
                request.setAttribute(AppConstants.ATTR_REMEMBERED_EMAIL, currentUser.toString());
            }
        }
        if (request.getAttribute(AppConstants.ATTR_REMEMBERED_EMAIL) == null) {
            String rememberedEmail = RememberMeManager.resolveRememberedUser(request);
            if (rememberedEmail != null) {
                request.setAttribute(AppConstants.ATTR_REMEMBERED_EMAIL, rememberedEmail);
            }
        }
    }
}
