package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.security.CookieManager;
import com.pinguela.rentexpressweb.security.TwoFactorManager;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;
import com.pinguela.rentexpressweb.util.ValidationErrorBag;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/security/verify-2fa")
public class TwoFactorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(TwoFactorServlet.class);
    private static final int REMEMBER_ME_DAYS = 30;

    @Override
    /*
     * Muestra el formulario de verificación comprobando que exista un proceso de
     * doble factor pendiente; en caso contrario redirige al login.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepare(request, response);
        if (!hasPendingTwoFactor(request)) {
            response.sendRedirect(request.getContextPath() + AppConstants.ROUTE_PUBLIC_LOGIN);
            return;
        }
        exposeTwoFactorAttributes(request);
        forward(request, response);
    }

    @Override
    /*
     * Procesa el código introducido, validando vigencia y contenido antes de
     * completar el inicio de sesión.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepare(request, response);
        if (!hasPendingTwoFactor(request)) {
            response.sendRedirect(request.getContextPath() + AppConstants.ROUTE_PUBLIC_LOGIN);
            return;
        }

        ValidationErrorBag errors = new ValidationErrorBag();
        String code = param(request, "code");

        String expected = (String) SessionManager.get(request, AppConstants.ATTR_2FA_CODE);
        Long timestamp = (Long) SessionManager.get(request, AppConstants.ATTR_2FA_CODE_TIMESTAMP);
        UserDTO user = (UserDTO) SessionManager.get(request, AppConstants.ATTR_PENDING_USER);

        if (isEmpty(code)) {
            errors.add("code", MessageResolver.getMessage(request, "twofactor.error.invalid"));
        } else if (TwoFactorManager.isExpired(timestamp)) {
            errors.add("code", MessageResolver.getMessage(request, "twofactor.error.expired"));
        } else if (expected == null || !expected.equals(code.trim())) {
            errors.add("code", MessageResolver.getMessage(request, "twofactor.error.invalid"));
        }

        if (!errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            exposeTwoFactorAttributes(request);
            forward(request, response);
            return;
        }

        completeTwoFactorLogin(request, response, user);
    }

    /*
     * Permite aplicar configuraciones previas necesarias antes de atender la
     * petición.
     */
    private void prepare(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // La codificación se aplica de forma global a través de EncodingFilter.
    }

    /*
     * Comprueba si en sesión existe un usuario pendiente de completar el proceso
     * de doble factor.
     */
    private boolean hasPendingTwoFactor(HttpServletRequest request) {
        return SessionManager.get(request, AppConstants.ATTR_PENDING_USER) instanceof UserDTO;
    }

    /*
     * Expone en la request los datos necesarios para mostrar el formulario de
     * verificación (principalmente el email pendiente).
     */
    private void exposeTwoFactorAttributes(HttpServletRequest request) {
        UserDTO user = (UserDTO) SessionManager.get(request, AppConstants.ATTR_PENDING_USER);
        request.setAttribute("pendingEmail", user != null ? user.getEmail() : null);
    }

    /*
     * Finaliza el proceso de autenticación estableciendo la sesión del usuario,
     * gestionando la cookie de "remember me" y redirigiendo al destino adecuado.
     */
    private void completeTwoFactorLogin(HttpServletRequest request, HttpServletResponse response, UserDTO user)
            throws IOException {
        if (user == null) {
            response.sendRedirect(request.getContextPath() + AppConstants.ROUTE_PUBLIC_LOGIN);
            return;
        }

        SessionManager.set(request, AppConstants.ATTR_CURRENT_USER, user);
        SessionManager.remove(request, AppConstants.ATTR_CURRENT_EMPLOYEE);

        SessionManager.remove(request, AppConstants.ATTR_PENDING_USER);
        SessionManager.remove(request, AppConstants.ATTR_2FA_CODE);
        SessionManager.remove(request, AppConstants.ATTR_2FA_CODE_TIMESTAMP);

        Boolean remember = (Boolean) SessionManager.get(request, AppConstants.ATTR_PENDING_REMEMBER);
        SessionManager.remove(request, AppConstants.ATTR_PENDING_REMEMBER);
        if (Boolean.TRUE.equals(remember)) {
            CookieManager.addCookie(request, response, AppConstants.COOKIE_REMEMBER_USER, user.getEmail(), REMEMBER_ME_DAYS);
        } else {
            CookieManager.removeCookie(request, response, AppConstants.COOKIE_REMEMBER_USER);
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("El usuario {} ha completado la verificación 2FA inicial", user.getEmail());
        }

        redirectAfterLogin(request, response, Views.PUBLIC_INDEX);
    }

    /*
     * Redirige a la ruta almacenada en sesión tras completar el doble factor o al
     * destino por defecto cuando no existe esa información.
     */
    private void redirectAfterLogin(HttpServletRequest request, HttpServletResponse response, String defaultPath)
            throws IOException {
        Object target = SessionManager.get(request, AppConstants.ATTR_REDIRECT_TARGET);
        if (target instanceof String) {
            String redirectPath = (String) target;
            if (redirectPath.length() > 0 && redirectPath.charAt(0) == '/') {
                SessionManager.remove(request, AppConstants.ATTR_REDIRECT_TARGET);
                response.sendRedirect(request.getContextPath() + redirectPath);
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + defaultPath);
    }

    /*
     * Reenvía a la vista del formulario de verificación 2FA.
     */
    private void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(Views.PUBLIC_VERIFY_2FA).forward(request, response);
    }

    /*
     * Recupera y limpia un parámetro de la petición eliminando espacios en blanco.
     */
    private String param(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value != null ? value.trim() : null;
    }

    /*
     * Determina si el valor de entrada es nulo o está vacío tras recortar
     * espacios.
     */
    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
