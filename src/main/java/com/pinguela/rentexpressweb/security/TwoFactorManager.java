package com.pinguela.rentexpressweb.security;

import java.text.MessageFormat;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.MailService;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Centraliza la generación y envío de códigos 2FA. Gestiona el estado asociado
 * en sesión y delega el envío del correo en {@link MailService}.
 */
public final class TwoFactorManager {

    private static final Logger LOGGER = LogManager.getLogger(TwoFactorManager.class);

    /** Duración por defecto del código en milisegundos (5 minutos). */
    public static final long CODE_TTL_MILLIS = 5 * 60 * 1000L;

    private static final int CODE_MAX_VALUE = 1_000_000;

    private TwoFactorManager() {
    }

    /**
     * Genera un código numérico de 6 dígitos.
     *
     * @return código 2FA formateado con ceros iniciales
     */
    public static String generateCode() {
        int value = ThreadLocalRandom.current().nextInt(CODE_MAX_VALUE);
        return String.format("%06d", Integer.valueOf(value));
    }

    /**
     * Inicia el flujo de doble verificación para el usuario indicado. Se genera
     * y almacena en sesión el código temporal y se envía un correo electrónico
     * con dicho código.
     *
     * @param request          petición HTTP asociada
     * @param user             usuario autenticado o recién registrado
     * @param rememberSelected indica si se seleccionó "recordarme"
     * @param mailService      servicio de correo configurado
     * @return {@code true} si el correo se envió correctamente
     */
    public static boolean startTwoFactor(HttpServletRequest request, UserDTO user, boolean rememberSelected,
            MailService mailService) {
        if (request == null || user == null) {
            LOGGER.warn("Unable to initialise 2FA without request or user context");
            return false;
        }

        SessionManager.set(request, AppConstants.ATTR_PENDING_USER, user);
        SessionManager.set(request, AppConstants.ATTR_PENDING_REMEMBER,
                rememberSelected ? Boolean.TRUE : Boolean.FALSE);

        String code = generateCode();
        SessionManager.set(request, AppConstants.ATTR_2FA_CODE, code);
        SessionManager.set(request, AppConstants.ATTR_2FA_CODE_TIMESTAMP, System.currentTimeMillis());

        if (mailService == null) {
            LOGGER.error("MailService not configured. Unable to send 2FA code for {}", user.getEmail());
            clearTwoFactorState(request);
            return false;
        }

        String email = user.getEmail();
        if (email == null || email.trim().isEmpty()) {
            LOGGER.error("Missing email for user {}. 2FA cannot be delivered", user.getUserId());
            clearTwoFactorState(request);
            return false;
        }

        String subject = MessageResolver.getMessage(request, "twofactor.email.subject", "RentExpress 2FA code");
        String bodyTemplate = MessageResolver.getMessage(request, "twofactor.email.body",
                "Your verification code is {1}");
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String message = MessageFormat.format(bodyTemplate, firstName, code);

        boolean sent = mailService.send(email, subject, message);
        if (!sent) {
            LOGGER.error("Failed to send 2FA code to {}", email);
            clearTwoFactorState(request);
        } else if (LOGGER.isInfoEnabled()) {
            LOGGER.info("2FA code sent to {}", email);
        }

        return sent;
    }

    /**
     * Limpia de la sesión cualquier resto del flujo 2FA pendiente.
     *
     * @param request petición HTTP
     */
    public static void clearTwoFactorState(HttpServletRequest request) {
        SessionManager.remove(request, AppConstants.ATTR_PENDING_USER);
        SessionManager.remove(request, AppConstants.ATTR_PENDING_REMEMBER);
        SessionManager.remove(request, AppConstants.ATTR_2FA_CODE);
        SessionManager.remove(request, AppConstants.ATTR_2FA_CODE_TIMESTAMP);
    }

    /**
     * Comprueba si el código almacenado en sesión ha caducado.
     *
     * @param timestamp instante en milisegundos cuando se generó el código
     * @return {@code true} si el código ha caducado o el valor es nulo
     */
    public static boolean isExpired(Long timestamp) {
        if (timestamp == null) {
            return true;
        }
        long age = System.currentTimeMillis() - timestamp.longValue();
        return age > CODE_TTL_MILLIS;
    }
}

