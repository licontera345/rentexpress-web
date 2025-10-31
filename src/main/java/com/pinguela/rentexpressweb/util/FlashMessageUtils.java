package com.pinguela.rentexpressweb.util;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.security.SessionManager;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utilidad para trasladar los mensajes flash almacenados en sesión al scope de
 * la petición antes de renderizar una vista.
 */
public final class FlashMessageUtils {

    private FlashMessageUtils() {
    }

    public static void transferToRequest(HttpServletRequest request) {
        if (request == null) {
            return;
        }
        moveAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        moveAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        moveAttribute(request, AppConstants.ATTR_FLASH_INFO);
    }

    private static void moveAttribute(HttpServletRequest request, String name) {
        Object value = SessionManager.getAttribute(request, name);
        if (value != null) {
            request.setAttribute(name, value);
            SessionManager.removeAttribute(request, name);
        }
    }
}
