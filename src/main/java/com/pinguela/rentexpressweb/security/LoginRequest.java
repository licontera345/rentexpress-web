package com.pinguela.rentexpressweb.security;

import java.util.Collections;
import java.util.Map;

/**
 * Representa los datos enviados en el formulario de login.
 */
public final class LoginRequest {

    private final String email;
    private final String password;
    private final Map<String, String> errors;

    public LoginRequest(String email, String password, Map<String, String> errors) {
        this.email = email;
        this.password = password;
        this.errors = errors;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public Map<String, String> getUnmodifiableErrors() {
        return Collections.unmodifiableMap(errors);
    }
}
