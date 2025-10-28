package com.pinguela.rentexpressweb.security;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.util.PasswordEncoder;

import jakarta.servlet.ServletContext;

/**
 * Almacenamiento en memoria (contexto de aplicación) de credenciales simuladas.
 */
public final class CredentialStore {

    private CredentialStore() {
    }

    public static void ensureCredential(ServletContext context, String email, String rawPassword) {
        if (context == null || email == null || rawPassword == null) {
            return;
        }
        String key = sanitize(email);
        rememberEmail(context, key);
        Map<String, String> credentials = getCredentialMap(context);
        credentials.computeIfAbsent(key, ignored -> PasswordEncoder.hash(rawPassword));
    }

    public static void updatePassword(ServletContext context, String email, String rawPassword) {
        if (context == null || email == null || rawPassword == null) {
            return;
        }
        String key = sanitize(email);
        rememberEmail(context, key);
        Map<String, String> credentials = getCredentialMap(context);
        credentials.put(key, PasswordEncoder.hash(rawPassword));
    }

    public static String findHashedPassword(ServletContext context, String email) {
        if (context == null || email == null) {
            return null;
        }
        return getCredentialMap(context).get(sanitize(email));
    }

    public static boolean isKnownEmail(ServletContext context, String email) {
        if (context == null || email == null) {
            return false;
        }
        String key = sanitize(email);
        if (getKnownEmails(context).contains(key)) {
            return true;
        }
        return getCredentialMap(context).containsKey(key);
    }

    public static void rememberEmail(ServletContext context, String email) {
        if (context == null || email == null) {
            return;
        }
        getKnownEmails(context).add(sanitize(email));
    }

    private static Map<String, String> getCredentialMap(ServletContext context) {
        @SuppressWarnings("unchecked")
        Map<String, String> credentials = (Map<String, String>) context.getAttribute(AppConstants.CONTEXT_CREDENTIALS);
        if (credentials == null) {
            credentials = new ConcurrentHashMap<>();
            context.setAttribute(AppConstants.CONTEXT_CREDENTIALS, credentials);
        }
        return credentials;
    }

    private static Set<String> getKnownEmails(ServletContext context) {
        @SuppressWarnings("unchecked")
        Set<String> emails = (Set<String>) context.getAttribute(AppConstants.CONTEXT_KNOWN_EMAILS);
        if (emails == null) {
            emails = ConcurrentHashMap.newKeySet();
            context.setAttribute(AppConstants.CONTEXT_KNOWN_EMAILS, emails);
        }
        return emails;
    }

    private static String sanitize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
