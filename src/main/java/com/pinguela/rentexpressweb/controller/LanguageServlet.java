package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Servlet implementation class LanguageServlet
 */
@WebServlet("/app/settings/language")
public class LanguageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final List<String> SUPPORTED_LANGUAGES = Arrays.asList("es", "en", "fr");

    private static final String HOME_PATH = "/app/home";

    public LanguageServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestedLanguage = request.getParameter(AppConstants.PARAM_LANGUAGE);
        if (requestedLanguage != null) {
            String normalized = requestedLanguage.toLowerCase(Locale.ROOT);
            if (SUPPORTED_LANGUAGES.contains(normalized)) {
                SessionManager.set(request, AppConstants.ATTR_LOCALE, normalized);
                SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                        MessageResolver.getMessage(request, "flash.language.updated",
                                normalized.toUpperCase(Locale.ROOT)));
                Cookie localeCookie = new Cookie(AppConstants.ATTR_LOCALE, normalized);
                localeCookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
                localeCookie.setMaxAge(60 * 60 * 24 * 365);
                localeCookie.setHttpOnly(true);
                localeCookie.setSecure(request.isSecure());
                response.addCookie(localeCookie);
            } else {
                SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                        MessageResolver.getMessage(request, "flash.language.unsupported"));
            }
        }

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.trim().isEmpty()) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + HOME_PATH);
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
