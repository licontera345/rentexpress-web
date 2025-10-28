package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.security.RememberMeManager;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.util.Views;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/app/auth/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DEMO_EMAIL = "demo@rentexpress.com";
    private static final String DEMO_PASSWORD = "RentExpress123";

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
            errors.add("Credenciales no válidas. Prueba con la cuenta demo.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Inicia sesión");
            request.setAttribute(AppConstants.ATTR_REMEMBERED_EMAIL, email);
            request.getRequestDispatcher(Views.PUBLIC_LOGIN).forward(request, response);
            return;
        }

        SessionManager.setAttribute(request, AppConstants.ATTR_CURRENT_USER, email);
        if (remember) {
            RememberMeManager.rememberUser(response, email);
        } else {
            RememberMeManager.forgetUser(response);
        }

        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                "¡Bienvenido de nuevo! Has accedido con la cuenta demo.");
        response.sendRedirect(request.getContextPath() + SecurityConstants.HOME_ENDPOINT);
    }

    private boolean authenticate(String email, String password) {
        return DEMO_EMAIL.equalsIgnoreCase(email != null ? email.trim() : null)
                && DEMO_PASSWORD.equals(password);
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
    }
}
