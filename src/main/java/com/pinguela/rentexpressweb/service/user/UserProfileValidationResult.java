package com.pinguela.rentexpressweb.service.user;

import jakarta.servlet.http.Part;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Resultado de la validación del formulario de perfil de usuario.
 */
public class UserProfileValidationResult {

    private final Map<String, String> formValues;
    private final Map<String, String> errors;
    private final String fullName;
    private final String phone;
    private final String email;
    private final String sanitizedPassword;
    private final Part avatarPart;

    public UserProfileValidationResult(Map<String, String> formValues, Map<String, String> errors,
            String fullName, String phone, String email, String sanitizedPassword, Part avatarPart) {
        this.formValues = formValues == null ? Collections.<String, String>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<String, String>(formValues));
        this.errors = errors == null ? Collections.<String, String>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<String, String>(errors));
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.sanitizedPassword = sanitizedPassword;
        this.avatarPart = avatarPart;
    }

    public Map<String, String> getFormValues() {
        return formValues;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getSanitizedPassword() {
        return sanitizedPassword;
    }

    public Part getAvatarPart() {
        return avatarPart;
    }
}
