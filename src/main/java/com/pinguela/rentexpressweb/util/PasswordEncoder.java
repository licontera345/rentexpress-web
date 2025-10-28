package com.pinguela.rentexpressweb.util;

import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * Utilidad sencilla para codificar y verificar contraseñas con Jasypt.
 */
public final class PasswordEncoder {

    private PasswordEncoder() {
    }

    public static String hash(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
        return encryptor.encryptPassword(rawPassword);
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
        return encryptor.checkPassword(rawPassword, hashedPassword);
    }
}
