package com.pinguela.rentexpressweb.service.user;

import com.pinguela.rentexpressweb.constants.MediaConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.security.CredentialStore;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.util.ControllerUtils;
import com.pinguela.rentexpressweb.util.ImageStorage;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servicio auxiliar para gestionar el perfil básico de usuario almacenado en sesión.
 */
public class UserProfileService {

    private static final Logger LOGGER = LogManager.getLogger(UserProfileService.class);

    public static final String KEY_ID = "id";
    public static final String KEY_FULL_NAME = "fullName";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ROLE = "role";
    public static final String KEY_AVATAR = "avatarPath";

    private static final String DEFAULT_FULL_NAME = "Cliente Demo";
    private static final String DEFAULT_PHONE = "+34 600 000 000";
    private static final String DEFAULT_ROLE = "CLIENT";

    public Map<String, String> getOrCreateProfile(HttpServletRequest request, String email) {
        @SuppressWarnings("unchecked")
        Map<String, String> profile = (Map<String, String>) SessionManager.getAttribute(request,
                UserConstants.ATTR_PROFILE_DATA);
        if (profile == null) {
            profile = new HashMap<String, String>();
            SessionManager.setAttribute(request, UserConstants.ATTR_PROFILE_DATA, profile);
        }

        if (ControllerUtils.isBlank(profile.get(KEY_ID))) {
            profile.put(KEY_ID, generateIdentifier(email));
        }
        if (ControllerUtils.isBlank(profile.get(KEY_FULL_NAME))) {
            profile.put(KEY_FULL_NAME, DEFAULT_FULL_NAME);
        }
        if (!profile.containsKey(KEY_PHONE)) {
            profile.put(KEY_PHONE, DEFAULT_PHONE);
        }
        if (ControllerUtils.isBlank(profile.get(KEY_EMAIL))) {
            profile.put(KEY_EMAIL, email);
        }
        if (ControllerUtils.isBlank(profile.get(KEY_ROLE))) {
            profile.put(KEY_ROLE, DEFAULT_ROLE);
        }
        if (!profile.containsKey(KEY_AVATAR)) {
            profile.put(KEY_AVATAR, "");
        }
        return profile;
    }

    public String applyProfileUpdates(HttpServletRequest request, ServletContext servletContext,
            Map<String, String> profile, UserProfileValidationResult validationResult) {
        profile.put(KEY_FULL_NAME, validationResult.getFullName());
        profile.put(KEY_PHONE, validationResult.getPhone() != null ? validationResult.getPhone() : "");
        profile.put(KEY_EMAIL, profile.get(KEY_EMAIL));

        String sanitizedPassword = validationResult.getSanitizedPassword();
        if (sanitizedPassword != null && !sanitizedPassword.isEmpty()) {
            CredentialStore.updatePassword(servletContext, profile.get(KEY_EMAIL), sanitizedPassword);
        }

        Part avatarPart = validationResult.getAvatarPart();
        if (avatarPart != null && avatarPart.getSize() > 0) {
            try {
                String identifier = resolveProfileIdentifier(profile);
                String storedPath = ImageStorage.store(servletContext, avatarPart, MediaConstants.VALUE_ENTITY_USER,
                        identifier);
                profile.put(KEY_AVATAR, storedPath);
            } catch (IOException ex) {
                LOGGER.error("Error almacenando el avatar del usuario {}", profile.get(KEY_EMAIL), ex);
                return "No se pudo guardar la imagen seleccionada.";
            }
        }

        SessionManager.setAttribute(request, UserConstants.ATTR_PROFILE_DATA, profile);
        return null;
    }

    private String resolveProfileIdentifier(Map<String, String> profile) {
        String identifier = profile.get(KEY_ID);
        if (ControllerUtils.isBlank(identifier)) {
            identifier = generateIdentifier(profile.get(KEY_EMAIL));
            profile.put(KEY_ID, identifier);
        }
        return identifier;
    }

    private String generateIdentifier(String seed) {
        if (seed == null) {
            return UUID.randomUUID().toString();
        }
        String lower = seed.toLowerCase(Locale.ROOT);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lower.length(); i++) {
            char ch = lower.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')) {
                builder.append(ch);
            }
        }
        if (builder.length() == 0) {
            return UUID.randomUUID().toString();
        }
        return builder.toString();
    }
}
