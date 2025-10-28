package com.pinguela.rentexpressweb.constants;

/**
 * Constantes relacionadas con seguridad básica de la aplicación.
 */
public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String REMEMBER_ME_COOKIE = "rentexpress-remember";
    public static final int REMEMBER_ME_MAX_AGE = 60 * 60 * 24 * 30; // 30 días
    public static final String COOKIE_PATH = "/";

    public static final String LOGIN_ENDPOINT = "/app/auth/login";
    public static final String HOME_ENDPOINT = "/app/welcome";
}
