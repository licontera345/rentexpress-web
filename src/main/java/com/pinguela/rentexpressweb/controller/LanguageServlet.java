package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.security.CookieManager;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

/**
 * Gestiona el cambio de idioma almacenando la preferencia en sesión y cookie.
 */
@WebServlet(name = "LanguageServlet", urlPatterns = Views.SERVLET_LANGUAGE)
public class LanguageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(LanguageServlet.class);

    @Override
    /*
     * Procesa el cambio de idioma comprobando si el código recibido es válido,
     * guardando la preferencia en sesión y cookie, y redirigiendo al usuario a la
     * página de origen.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String language = request.getParameter(AppConstants.PARAM_LANGUAGE);
        if (!isSupported(language)) {
            language = resolveFromCookie(request);
        }

        SessionManager.set(request, AppConstants.ATTR_LANGUAGE, language);
        SessionManager.set(request, AppConstants.ATTR_LOCALE, language);

        CookieManager.addCookie(request, response, AppConstants.COOKIE_LANGUAGE, language, 365);

        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains(request.getContextPath())) {
            response.sendRedirect(referer);
            return;
        }

        response.sendRedirect(request.getContextPath() + Views.PUBLIC_INDEX);
    }

    @Override
    /*
     * Delegado del manejo POST al flujo GET para soportar formularios que envían
     * la selección de idioma mediante dicho método.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /*
     * Verifica si el código de idioma está entre los idiomas soportados por la
     * aplicación y registra trazas de depuración cuando no es válido.
     */
    private boolean isSupported(String language) {
        if (language == null || language.isEmpty()) {
            return false;
        }
        int i;
        for (i = 0; i < AppConstants.SUPPORTED_LANGUAGES.length; i++) {
            if (AppConstants.SUPPORTED_LANGUAGES[i].equalsIgnoreCase(language)) {
                return true;
            }
        }
        LOGGER.debug("Idioma no soportado: {}", language);
        return false;
    }

    /*
     * Obtiene el idioma preferido previamente almacenado en la cookie del
     * usuario, o devuelve el idioma por defecto si no existe o no es válido.
     */
    private String resolveFromCookie(HttpServletRequest request) {
        Cookie cookie = CookieManager.getCookie(request, AppConstants.COOKIE_LANGUAGE);
        if (cookie != null && isSupported(cookie.getValue())) {
            return cookie.getValue();
        }
        return AppConstants.DEFAULT_LANGUAGE;
    }
}
