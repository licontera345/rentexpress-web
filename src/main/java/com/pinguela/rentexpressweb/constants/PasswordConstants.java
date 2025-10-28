package com.pinguela.rentexpressweb.constants;

/**
 * Constantes relacionadas con el flujo de recuperación de contraseñas.
 */
public final class PasswordConstants {

    private PasswordConstants() {
    }

    public static final String PARAM_RESET_CODE = "code";
    public static final String PARAM_NEW_PASSWORD = "newPassword";
    public static final String PARAM_CONFIRM_PASSWORD = "confirmPassword";

    public static final String ATTR_FORGOT_ERRORS = "forgotPasswordErrors";
    public static final String ATTR_FORGOT_EMAIL = "forgotPasswordEmail";
    public static final String ATTR_VERIFY_ERRORS = "verifyResetErrors";
    public static final String ATTR_SUBMITTED_CODE = "submittedResetCode";
    public static final String ATTR_RESET_ERRORS = "resetPasswordErrors";
    public static final String ATTR_PENDING_EMAIL = "pendingResetEmail";
    public static final String ATTR_SECONDS_REMAINING = "resetSecondsRemaining";
}
