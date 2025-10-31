package com.pinguela.rentexpressweb.service.user;

import com.pinguela.rentexpressweb.constants.MediaConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.ControllerUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Encapsula las validaciones del formulario de perfil.
 */
public class UserProfileValidator {

    private static final Logger LOGGER = LogManager.getLogger(UserProfileValidator.class);

    public UserProfileValidationResult validate(HttpServletRequest request, Map<String, String> currentProfile)
            throws ServletException, IOException {
        Map<String, String> formValues = new LinkedHashMap<String, String>();
        Map<String, String> errors = new LinkedHashMap<String, String>();

        String fullName = ControllerUtils.trimToNull(request.getParameter(UserConstants.PARAM_FULL_NAME));
        String phone = ControllerUtils.trimToNull(request.getParameter(UserConstants.PARAM_PHONE));
        String email = ControllerUtils.trimToNull(request.getParameter(UserConstants.PARAM_EMAIL));
        String password = request.getParameter(UserConstants.PARAM_PASSWORD);

        formValues.put(UserProfileService.KEY_FULL_NAME, fullName != null ? fullName : "");
        formValues.put(UserProfileService.KEY_PHONE, phone != null ? phone : "");
        formValues.put(UserProfileService.KEY_EMAIL, email != null ? email : "");
        formValues.put(UserProfileService.KEY_ID, currentProfile.get(UserProfileService.KEY_ID));
        formValues.put(UserProfileService.KEY_ROLE, currentProfile.get(UserProfileService.KEY_ROLE));
        formValues.put(UserProfileService.KEY_AVATAR, currentProfile.get(UserProfileService.KEY_AVATAR));

        if (fullName == null) {
            errors.put(UserConstants.PARAM_FULL_NAME, "El nombre es obligatorio.");
        }

        if (phone != null && phone.length() > 20) {
            errors.put(UserConstants.PARAM_PHONE, "El teléfono no puede superar los 20 caracteres.");
        }

        if (email == null) {
            errors.put(UserConstants.PARAM_EMAIL, "El correo electrónico es obligatorio.");
        } else if (!isValidEmail(email)) {
            errors.put(UserConstants.PARAM_EMAIL, "Indica un correo electrónico válido.");
        } else if (!email.equalsIgnoreCase(currentProfile.get(UserProfileService.KEY_EMAIL))) {
            errors.put(UserConstants.PARAM_EMAIL, "En esta demo el correo no se puede modificar.");
        }

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

        return new UserProfileValidationResult(formValues, errors, fullName, phone, email, sanitizedPassword,
                avatarPart);
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
}
