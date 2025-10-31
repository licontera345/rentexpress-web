package com.pinguela.rentexpressweb.constants;

/**
 * Constantes relacionadas con seguridad básica de la aplicación.
 */
public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String LOGIN_ENDPOINT = "/app/auth/login";
    public static final String HOME_ENDPOINT = "/app/home";

    public static final int VERIFICATION_CODE_LENGTH = 6;
    public static final int VERIFICATION_CODE_VALIDITY_SECONDS = 60;
}
