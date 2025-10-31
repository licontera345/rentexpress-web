package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.util.SessionManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/settings/language")
public class LanguageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final List<String> SUPPORTED_LANGUAGES = Arrays.asList("es", "en", "fr");
    private static final int ONE_YEAR_SECONDS = 60 * 60 * 24 * 365;
    private static final String HOME_ROUTE = "/app/home";
    private static final String MESSAGE_LANGUAGE_UPDATED = "Idioma actualizado correctamente.";
    private static final String MESSAGE_LANGUAGE_UNSUPPORTED = "El idioma seleccionado no está disponible.";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestedLanguage = request.getParameter(AppConstants.PARAM_LANGUAGE);
        if (requestedLanguage != null) {
            String normalized = requestedLanguage.toLowerCase(Locale.ROOT);
            if (SUPPORTED_LANGUAGES.contains(normalized)) {
                SessionManager.set(request, AppConstants.ATTR_LOCALE, normalized);
                SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS, MESSAGE_LANGUAGE_UPDATED);
                Cookie localeCookie = new Cookie(AppConstants.ATTR_LOCALE, normalized);
                localeCookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
                localeCookie.setMaxAge(ONE_YEAR_SECONDS);
                localeCookie.setHttpOnly(true);
                localeCookie.setSecure(request.isSecure());
                response.addCookie(localeCookie);
            } else {
                SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR, MESSAGE_LANGUAGE_UNSUPPORTED);
            }
        }
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.trim().isEmpty()) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + HOME_ROUTE);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
