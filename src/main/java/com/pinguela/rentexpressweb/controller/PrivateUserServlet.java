package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.MediaConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.security.CredentialStore;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.util.ImageStorage;
import com.pinguela.rentexpressweb.util.Views;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Zona privada de perfil de usuario con actualización básica y carga de avatar.
 */
@WebServlet("/app/users/private")
@MultipartConfig
public class PrivateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PrivateUserServlet.class);
    private static final String KEY_ID = "id";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_AVATAR = "avatarPath";

    public PrivateUserServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "Inicia sesión para acceder a tu perfil.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }

        disableCaching(response);
        exposeFlashMessages(request);

        Map<String, String> profile = getOrCreateProfile(request, currentUser.toString());
        Map<String, String> errors = getErrorsFromRequest(request);

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Mi perfil");
        request.setAttribute("account", profile);
        request.setAttribute("role", profile.get(KEY_ROLE));
        if (errors != null && !errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
        }

        request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "Inicia sesión para actualizar tu perfil.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }

        Map<String, String> profile = getOrCreateProfile(request, currentUser.toString());
        Map<String, String> formValues = new HashMap<String, String>();
        formValues.put(KEY_ID, profile.get(KEY_ID));
        formValues.put(KEY_ROLE, profile.get(KEY_ROLE));

        Map<String, String> errors = new LinkedHashMap<String, String>();

        String fullName = trimToNull(request.getParameter(UserConstants.PARAM_FULL_NAME));
        String phone = trimToNull(request.getParameter(UserConstants.PARAM_PHONE));
        String email = trimToNull(request.getParameter(UserConstants.PARAM_EMAIL));
        String password = request.getParameter(UserConstants.PARAM_PASSWORD);

        if (fullName == null) {
            errors.put(UserConstants.PARAM_FULL_NAME, "El nombre es obligatorio.");
        } else {
            formValues.put(KEY_FULL_NAME, fullName);
        }

        if (phone != null && phone.length() > 20) {
            errors.put(UserConstants.PARAM_PHONE, "El teléfono no puede superar los 20 caracteres.");
        }
        formValues.put(KEY_PHONE, phone != null ? phone : "");

        if (email == null) {
            errors.put(UserConstants.PARAM_EMAIL, "El correo electrónico es obligatorio.");
        } else if (!isValidEmail(email)) {
            errors.put(UserConstants.PARAM_EMAIL, "Indica un correo electrónico válido.");
        } else if (!email.equalsIgnoreCase(profile.get(KEY_EMAIL))) {
            errors.put(UserConstants.PARAM_EMAIL, "En esta demo el correo no se puede modificar.");
        }
        formValues.put(KEY_EMAIL, email != null ? email : "");

        String sanitizedPassword = password == null ? null : password.trim();
        if (sanitizedPassword != null && !sanitizedPassword.isEmpty() && sanitizedPassword.length() < 8) {
            errors.put(UserConstants.PARAM_PASSWORD, "La contraseña debe tener al menos 8 caracteres.");
        }

        Part avatarPart = null;
        try {
            avatarPart = request.getPart(MediaConstants.PARAM_IMAGE_FILE);
        } catch (IllegalStateException ex) {
            LOGGER.error("La subida de avatar excede el tamaño permitido", ex);
            errors.put(MediaConstants.PARAM_IMAGE_FILE, "El fichero es demasiado grande.");
        }

        if (avatarPart != null && avatarPart.getSize() > MediaConstants.MAX_IMAGE_SIZE_BYTES) {
            errors.put(MediaConstants.PARAM_IMAGE_FILE, "La imagen no debe superar los 2MB.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Mi perfil");
            request.setAttribute("account", formValues);
            request.setAttribute("role", formValues.get(KEY_ROLE));
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
            return;
        }

        profile.put(KEY_FULL_NAME, fullName);
        profile.put(KEY_PHONE, phone != null ? phone : "");
        profile.put(KEY_EMAIL, profile.get(KEY_EMAIL));

        if (sanitizedPassword != null && !sanitizedPassword.isEmpty()) {
            CredentialStore.updatePassword(getServletContext(), profile.get(KEY_EMAIL), sanitizedPassword);
        }

        if (avatarPart != null && avatarPart.getSize() > 0) {
            try {
                String identifier = resolveProfileIdentifier(profile);
                String storedPath = ImageStorage.store(getServletContext(), avatarPart,
                        MediaConstants.VALUE_ENTITY_USER, identifier);
                profile.put(KEY_AVATAR, storedPath);
            } catch (IOException ex) {
                LOGGER.error("Error almacenando el avatar del usuario {}", profile.get(KEY_EMAIL), ex);
                request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Mi perfil");
                request.setAttribute("account", profile);
                request.setAttribute("role", profile.get(KEY_ROLE));
                errors.put(MediaConstants.PARAM_IMAGE_FILE, "No se pudo guardar la imagen seleccionada.");
                request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
                request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
                return;
            }
        }

        SessionManager.setAttribute(request, UserConstants.ATTR_PROFILE_DATA, profile);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                "Perfil actualizado correctamente.");

        response.sendRedirect(request.getContextPath() + "/app/users/private");
    }

    private Map<String, String> getOrCreateProfile(HttpServletRequest request, String email) {
        @SuppressWarnings("unchecked")
        Map<String, String> profile = (Map<String, String>) SessionManager.getAttribute(request,
                UserConstants.ATTR_PROFILE_DATA);
        if (profile == null) {
            profile = new HashMap<String, String>();
            profile.put(KEY_ID, generateIdentifier(email));
            profile.put(KEY_FULL_NAME, "Cliente Demo");
            profile.put(KEY_PHONE, "+34 600 000 000");
            profile.put(KEY_EMAIL, email);
            profile.put(KEY_ROLE, "CLIENT");
            SessionManager.setAttribute(request, UserConstants.ATTR_PROFILE_DATA, profile);
        }
        return profile;
    }

    private Map<String, String> getErrorsFromRequest(HttpServletRequest request) {
        Object errors = request.getAttribute(AppConstants.ATTR_FORM_ERRORS);
        if (errors instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) errors;
            return map;
        }
        return null;
    }

    private void exposeFlashMessages(HttpServletRequest request) {
        Object success = SessionManager.getAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        if (success != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_SUCCESS, success);
            SessionManager.removeAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        }
        Object error = SessionManager.getAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        if (error != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, error);
            SessionManager.removeAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        }
    }

    private void disableCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String trimmed = email.trim();
        int atIndex = trimmed.indexOf('@');
        int dotIndex = trimmed.lastIndexOf('.');
        return atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < trimmed.length() - 1;
    }

    private String resolveProfileIdentifier(Map<String, String> profile) {
        String identifier = profile.get(KEY_ID);
        if (identifier == null || identifier.trim().isEmpty()) {
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
