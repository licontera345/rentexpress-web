package com.pinguela.rentexpressweb.util;

import java.math.BigDecimal;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.security.CookieManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;

/*
 * Utilidades mínimas para acceder a parámetros de la petición y resolver
 * preferencias comunes a partir de los datos recibidos.
 */
public final class RequestHelper {

    private RequestHelper() {
    }

    /*
     * Obtiene un parámetro de la request eliminando espacios en blanco y
     * devolviendo null si queda vacío.
     */
    public static String param(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            return null;
        }
        String value = request.getParameter(name);
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /*
     * Convierte un texto en Integer manejando los posibles errores de formato.
     */
    public static Integer parseInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /*
     * Convierte un texto en BigDecimal devolviendo null cuando el formato no es
     * válido.
     */
    public static BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /*
     * Determina el idioma de trabajo priorizando sesión, cookie y cabecera de la
     * petición antes de recurrir al valor por defecto.
     */
    public static String resolveLanguage(HttpServletRequest request) {
        Object stored = SessionManager.get(request, AppConstants.ATTR_LANGUAGE);
        if (stored instanceof String && isSupportedLanguage((String) stored)) {
            return (String) stored;
        }
        Cookie languageCookie = CookieManager.getCookie(request, AppConstants.COOKIE_LANGUAGE);
        if (languageCookie != null && isSupportedLanguage(languageCookie.getValue())) {
            return languageCookie.getValue();
        }
        if (request != null && request.getLocale() != null) {
            String language = request.getLocale().getLanguage();
            if (isSupportedLanguage(language)) {
                return language;
            }
        }
        return AppConstants.DEFAULT_LANGUAGE;
    }

    /*
     * Comprueba si el idioma indicado forma parte del listado soportado por la
     * aplicación.
     */
    private static boolean isSupportedLanguage(String language) {
        if (language == null || language.isEmpty()) {
            return false;
        }
        for (int i = 0; i < AppConstants.SUPPORTED_LANGUAGES.length; i++) {
            if (AppConstants.SUPPORTED_LANGUAGES[i].equalsIgnoreCase(language)) {
                return true;
            }
        }
        return false;
    }
}
