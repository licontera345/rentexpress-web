package com.pinguela.rentexpressweb.security;

import com.pinguela.rentexpres.model.UserDTO;

import jakarta.servlet.ServletContext;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Encapsula la lógica de autenticación de usuarios y la gestión de
 * identificadores recordados.
 */
public final class LoginAuthenticator {

    private static final Logger LOGGER = LogManager.getLogger(LoginAuthenticator.class);

    private LoginAuthenticator() {
    }

    public static UserDTO authenticate(ServletContext context, UserServiceFacade userService, String identifier,
            String password) {
        if (userService == null || identifier == null || password == null) {
            return null;
        }
        String trimmedIdentifier = identifier.trim();
        if (trimmedIdentifier.isEmpty()) {
            return null;
        }
        String normalizedIdentifier = trimmedIdentifier.toLowerCase(Locale.ROOT);
        try {
            UserDTO user = userService.authenticate(trimmedIdentifier, password);
            if (user == null) {
                LOGGER.warn("Intento de acceso fallido para {}", normalizedIdentifier);
                return null;
            }
            if (Boolean.FALSE.equals(user.getActiveStatus())) {
                LOGGER.warn("Intento de acceso para usuario inactivo {}", normalizedIdentifier);
                return null;
            }
            rememberResolvedIdentifiers(context, user, normalizedIdentifier);
            return user;
        } catch (Exception ex) {
            LOGGER.error("Error autenticando al usuario {}", normalizedIdentifier, ex);
            return null;
        }
    }

    public static String resolveLoginEmail(UserDTO user, String providedIdentifier) {
        if (user != null && user.getEmail() != null) {
            String email = user.getEmail().trim();
            if (!email.isEmpty()) {
                return email;
            }
        }
        if (providedIdentifier != null) {
            String sanitized = providedIdentifier.trim();
            if (!sanitized.isEmpty()) {
                return sanitized;
            }
        }
        return null;
    }

    public static void updateCredential(ServletContext context, String resolvedEmail, String fallbackIdentifier,
            String password) {
        if (context == null || password == null) {
            return;
        }
        if (resolvedEmail != null) {
            CredentialStore.updatePassword(context, resolvedEmail, password);
        } else if (fallbackIdentifier != null && !fallbackIdentifier.trim().isEmpty()) {
            CredentialStore.updatePassword(context, fallbackIdentifier, password);
        }
    }

    public static String normalize(String email) {
        if (email == null) {
            return null;
        }
        String trimmed = email.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }

    private static void rememberResolvedIdentifiers(ServletContext context, UserDTO user, String identifier) {
        if (context == null) {
            return;
        }
        if (identifier != null) {
            CredentialStore.rememberEmail(context, identifier);
        }
        if (user != null && user.getEmail() != null) {
            CredentialStore.rememberEmail(context, user.getEmail());
        }
        if (user != null && user.getUsername() != null) {
            CredentialStore.rememberEmail(context, user.getUsername());
        }
    }

    /**
     * Pequeña fachada para permitir tests y aislar la dependencia con UserService.
     */
    public interface UserServiceFacade {
        UserDTO authenticate(String identifier, String password) throws Exception;
    }
}
