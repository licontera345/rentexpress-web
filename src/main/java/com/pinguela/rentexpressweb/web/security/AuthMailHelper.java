package com.pinguela.rentexpressweb.web.security;

import com.pinguela.rentexpres.service.MailService;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;

/**
 * Gestiona el envío de correos relacionados con la autenticación.
 */
final class AuthMailHelper {

    private AuthMailHelper() {
    }

    static boolean sendVerification(MailService mailService, HttpServletRequest request, String email, String code,
            Logger logger) {
        if (mailService == null || request == null || email == null || code == null) {
            return false;
        }
        String subject = MessageResolver.getMessage(request, "mail.2fa.subject");
        String body = MessageResolver.getMessage(request, "mail.2fa.body", code,
                Integer.valueOf(SecurityConstants.TWO_FA_CODE_VALIDITY_SECONDS));
        try {
            if (!mailService.send(email, subject, body)) {
                logger.error("No se pudo enviar el correo 2FA a {}", email);
                return false;
            }
            return true;
        } catch (RuntimeException ex) {
            logger.error("Error inesperado enviando el correo 2FA a {}", email, ex);
            return false;
        }
    }

    static String buildVerificationInfo(HttpServletRequest request, String email, boolean resent) {
        String key = resent ? "info.login.2fa.resent" : "info.login.2fa.sent";
        return MessageResolver.getMessage(request, key, email,
                Integer.valueOf(SecurityConstants.TWO_FA_CODE_VALIDITY_SECONDS));
    }
}
