package com.pinguela.rentexpressweb.util;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad sencilla para codificar y verificar contraseñas usando BCrypt como
 * algoritmo principal y compatibilidad con hashes existentes de Jasypt.
 */
public final class PasswordEncoder {

    private PasswordEncoder() {
    }

    private static final String BCRYPT_PREFIX = "$2";

    public static String hash(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        if (hashedPassword.startsWith(BCRYPT_PREFIX)) {
            try {
                return BCrypt.checkpw(rawPassword, hashedPassword);
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }
        StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
        return encryptor.checkPassword(rawPassword, hashedPassword);
    }
}
