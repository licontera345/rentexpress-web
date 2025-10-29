package com.pinguela.rentexpressweb.util;

import com.pinguela.rentexpressweb.constants.AppConstants;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utilidad centralizada para resolver mensajes internacionalizados desde los servlets.
 */
public final class MessageResolver {

    private static final String BUNDLE_BASE_NAME = "i18n.Messages";

    private MessageResolver() {
    }

    public static String getMessage(HttpServletRequest request, String key) {
        return getMessage(request, key, (Object[]) null);
    }

    public static String getMessage(HttpServletRequest request, String key, Object... arguments) {
        if (key == null) {
            return "";
        }
        Locale locale = resolveLocale(request);
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
        String pattern = key;
        if (bundle != null) {
            try {
                pattern = bundle.getString(key);
            } catch (MissingResourceException ex) {
                pattern = key;
            }
        }
        if (arguments == null || arguments.length == 0) {
            return pattern;
        }
        return MessageFormat.format(pattern, arguments);
    }

    private static Locale resolveLocale(HttpServletRequest request) {
        if (request == null) {
            return Locale.getDefault();
        }

        Object sessionLocale = request.getSession(false) != null
                ? request.getSession(false).getAttribute(AppConstants.ATTR_LOCALE)
                : null;
        Locale locale = toLocale(sessionLocale);
        if (locale != null) {
            return locale;
        }

        Cookie localeCookie = CookieUtils.findCookie(request, AppConstants.ATTR_LOCALE);
        if (localeCookie != null) {
            locale = toLocale(localeCookie.getValue());
            if (locale != null) {
                return locale;
            }
        }

        Locale requestLocale = request.getLocale();
        if (requestLocale != null) {
            return requestLocale;
        }
        return Locale.getDefault();
    }

    private static Locale toLocale(Object value) {
        if (value instanceof Locale) {
            return (Locale) value;
        }
        if (value instanceof String) {
            String text = ((String) value).trim();
            if (!text.isEmpty()) {
                return Locale.forLanguageTag(text.replace('_', '-'));
            }
        }
        return null;
    }
}
