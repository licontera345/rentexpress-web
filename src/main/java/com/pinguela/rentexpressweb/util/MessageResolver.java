package com.pinguela.rentexpressweb.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.pinguela.rentexpressweb.constants.AppConstants;

import jakarta.servlet.http.HttpServletRequest;

/*
 * Utilidad para resolver mensajes internacionalizados respetando el idioma
 * almacenado en sesión o el proporcionado por la petición HTTP.
 */
public final class MessageResolver {

    private static final String BUNDLE_BASE_NAME = "i18n.Messages";

    private MessageResolver() {
    }

    /*
     * Obtiene un mensaje traducido utilizando la propia clave como valor por
     * defecto.
     */
    public static String getMessage(HttpServletRequest request, String key) {
        return getMessage(request, key, key);
    }

    /*
     * Resuelve un mensaje internacionalizado y permite indicar un valor
     * alternativo si no se encuentra en los bundles configurados.
     */
    public static String getMessage(HttpServletRequest request, String key, String defaultValue) {
        Locale locale = resolveLocale(request);
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
        try {
            return bundle.getString(key);
        } catch (MissingResourceException ex) {
            ResourceBundle fallback = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.ROOT);
            if (fallback.containsKey(key)) {
                return fallback.getString(key);
            }
            return defaultValue != null ? defaultValue : key;
        }
    }

    /*
     * Determina el Locale a utilizar priorizando el almacenado en sesión, el
     * indicado en la petición y, en último término, el idioma por defecto.
     */
    private static Locale resolveLocale(HttpServletRequest request) {
        Object storedLocale = SessionManager.get(request, AppConstants.ATTR_LOCALE);
        if (storedLocale instanceof String) {
            String languageTag = ((String) storedLocale).trim();
            if (!languageTag.isEmpty()) {
                Locale resolved = Locale.forLanguageTag(languageTag);
                if (!resolved.getLanguage().isEmpty()) {
                    return resolved;
                }
            }
        }
        if (request != null) {
            Locale requestLocale = request.getLocale();
            if (requestLocale != null) {
                return requestLocale;
            }
        }
        return Locale.ROOT;
    }
}
