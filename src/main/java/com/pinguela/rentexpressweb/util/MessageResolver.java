package com.pinguela.rentexpressweb.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.pinguela.rentexpressweb.constants.AppConstants;

import jakarta.servlet.http.HttpServletRequest;

public final class MessageResolver {

    private static final String BUNDLE_BASE_NAME = "i18n.Messages";

    private MessageResolver() {
    }

    public static String getMessage(HttpServletRequest request, String key) {
        Locale locale = resolveLocale(request);
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
        try {
            return bundle.getString(key);
        } catch (MissingResourceException ex) {
            ResourceBundle fallback = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.ROOT);
            return fallback.containsKey(key) ? fallback.getString(key) : key;
        }
    }

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
