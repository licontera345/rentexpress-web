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

    public static final String DEMO_EMAIL = "demo@rentexpress.com";
    public static final String DEMO_PASSWORD = "RentExpress123";

    public static final String LOGIN_ENDPOINT = "/app/auth/login";
    public static final String HOME_ENDPOINT = "/app/welcome";

    public static final int TWO_FA_CODE_LENGTH = 6;
    public static final int TWO_FA_CODE_VALIDITY_SECONDS = 60;

    public static final String ATTR_PENDING_2FA_EMAIL = "pending2faEmail";
    public static final String ATTR_PENDING_2FA_CODE = "pending2faCode";
    public static final String ATTR_PENDING_2FA_EXPIRATION = "pending2faExpiration";
    public static final String ATTR_PENDING_2FA_REMEMBER = "pending2faRemember";

    public static final String PARAM_2FA_CODE = "code";
    public static final String PARAM_RESEND = "resend";
}
