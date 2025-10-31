package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.MediaConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.security.CredentialStore;
import com.pinguela.rentexpressweb.util.ImageStorage;
import com.pinguela.rentexpressweb.util.SessionManager;
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

@WebServlet("/app/users/private")
@MultipartConfig
public class PrivateProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PrivateProfileServlet.class);
    private static final String KEY_ID = "id";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_AVATAR = "avatarPath";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireUser(request, response, "Inicia sesión para acceder a tu perfil.")) {
            return;
        }

        disableCaching(response);
        copyFlashMessages(request);

        Object currentUser = SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        Map<String, String> profile = getOrCreateProfile(request, currentUser.toString());
        Map<String, String> errors = getErrorsFromRequest(request);

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Mi perfil");
        request.setAttribute("account", profile);
        request.setAttribute("role", profile.get(KEY_ROLE));
        if (errors != null && !errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
        }

        forward(request, response, Views.PRIVATE_USER_PROFILE);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireUser(request, response, "Inicia sesión para actualizar tu perfil.")) {
            return;
        }

        Object currentUser = SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        Map<String, String> profile = getOrCreateProfile(request, currentUser.toString());
        Map<String, String> formValues = new HashMap<String, String>();
        formValues.put(KEY_ID, profile.get(KEY_ID));
        formValues.put(KEY_ROLE, profile.get(KEY_ROLE));

        Map<String, String> errors = new LinkedHashMap<String, String>();

        String fullName = trimToNull(getTrimmedParameter(request, UserConstants.PARAM_FULL_NAME));
        String phone = getTrimmedParameter(request, UserConstants.PARAM_PHONE);
        String email = trimToNull(getTrimmedParameter(request, UserConstants.PARAM_EMAIL));
        String password = getTrimmedParameter(request, UserConstants.PARAM_PASSWORD);

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

        String sanitizedPassword = trimToNull(password);
        if (sanitizedPassword != null && sanitizedPassword.length() < 8) {
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
            forward(request, response, Views.PRIVATE_USER_PROFILE);
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
                forward(request, response, Views.PRIVATE_USER_PROFILE);
                return;
            }
        }

        SessionManager.set(request, UserConstants.ATTR_PROFILE_DATA, profile);
        SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS, "Perfil actualizado correctamente.");

        redirect(request, response, "/app/users/private");
    }

    private Map<String, String> getOrCreateProfile(HttpServletRequest request, String email) {
        @SuppressWarnings("unchecked")
        Map<String, String> profile = (Map<String, String>) SessionManager.get(request,
                UserConstants.ATTR_PROFILE_DATA);
        if (profile == null) {
            profile = new HashMap<String, String>();
            profile.put(KEY_ID, generateIdentifier(email));
            profile.put(KEY_FULL_NAME, "Cliente Demo");
            profile.put(KEY_PHONE, "+34 600 000 000");
            profile.put(KEY_EMAIL, email);
            profile.put(KEY_ROLE, "CLIENT");
            profile.put(KEY_AVATAR, "");
            SessionManager.set(request, UserConstants.ATTR_PROFILE_DATA, profile);
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

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        return normalized.contains("@") && normalized.indexOf('@') < normalized.length() - 3;
    }

    private String resolveProfileIdentifier(Map<String, String> profile) {
        String identifier = profile.get(KEY_ID);
        if (identifier != null && !identifier.trim().isEmpty()) {
            return identifier;
        }
        return UUID.randomUUID().toString();
    }

    private boolean requireUser(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws IOException {
        if (SessionManager.get(request, AppConstants.ATTR_CURRENT_USER) != null) {
            return true;
        }
        if (errorMessage != null && !errorMessage.isEmpty()) {
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR, errorMessage);
        }
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    private void disableCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private void copyFlashMessages(HttpServletRequest request) {
        transferFlashAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        transferFlashAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        transferFlashAttribute(request, AppConstants.ATTR_FLASH_INFO);
    }

    private void transferFlashAttribute(HttpServletRequest request, String name) {
        Object value = SessionManager.get(request, name);
        if (value != null) {
            request.setAttribute(name, value);
            SessionManager.remove(request, name);
        }
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher(view).forward(request, response);
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String location) throws IOException {
        String target = location;
        if (!location.startsWith("http://") && !location.startsWith("https://")) {
            target = request.getContextPath() + location;
        }
        response.sendRedirect(target);
    }

    private String getTrimmedParameter(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            return null;
        }
        return trim(request.getParameter(name));
    }

    private String trim(String value) {
        return value != null ? value.trim() : null;
    }

    private String trimToNull(String value) {
        String trimmed = trim(value);
        return trimmed == null || trimmed.isEmpty() ? null : trimmed;
    }

    private String generateIdentifier(String seed) {
        if (seed == null) {
            return "profile";
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
            builder.append("profile");
        }
        return builder.toString();
    }
}
