package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.MediaConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.service.user.UserProfileService;
import com.pinguela.rentexpressweb.service.user.UserProfileValidationResult;
import com.pinguela.rentexpressweb.service.user.UserProfileValidator;
import com.pinguela.rentexpressweb.util.ControllerUtils;
import com.pinguela.rentexpressweb.util.UserActivityTracker;
import com.pinguela.rentexpressweb.util.Views;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Zona privada de perfil de usuario con actualización básica y carga de avatar.
 */
@WebServlet("/app/users/private")
@MultipartConfig
public class PrivateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserProfileService profileService = new UserProfileService();
    private final UserProfileValidator profileValidator = new UserProfileValidator();

    public PrivateUserServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "Inicia sesión para acceder a tu perfil.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }

        ControllerUtils.disableCaching(response);
        ControllerUtils.exposeFlashMessages(request);

        Map<String, String> profile = profileService.getOrCreateProfile(request, currentUser.toString());
        Map<String, String> errors = getErrorsFromRequest(request);

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Mi perfil");
        request.setAttribute("account", profile);
        request.setAttribute("role", profile.get(UserProfileService.KEY_ROLE));
        if (errors != null && !errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
        }

        request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "Inicia sesión para actualizar tu perfil.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }

        Map<String, String> profile = profileService.getOrCreateProfile(request, currentUser.toString());

        UserProfileValidationResult validationResult = profileValidator.validate(request, profile);
        if (validationResult.hasErrors()) {
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Mi perfil");
            request.setAttribute("account", validationResult.getFormValues());
            request.setAttribute("role", validationResult.getFormValues().get(UserProfileService.KEY_ROLE));
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, validationResult.getErrors());
            request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
            return;
        }

        String avatarError = profileService.applyProfileUpdates(request, getServletContext(), profile,
                validationResult);
        if (avatarError != null) {
            Map<String, String> errors = new LinkedHashMap<String, String>();
            errors.put(MediaConstants.PARAM_IMAGE_FILE, avatarError);
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Mi perfil");
            request.setAttribute("account", profile);
            request.setAttribute("role", profile.get(UserProfileService.KEY_ROLE));
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
            return;
        }

        SessionManager.setAttribute(request, UserConstants.ATTR_PROFILE_DATA, profile);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                "Perfil actualizado correctamente.");
        UserActivityTracker.record(request, "home.dashboard.activity.profileUpdated", "bi bi-person-check-fill");

        response.sendRedirect(request.getContextPath() + "/app/users/private");
    }

    private Map<String, String> getErrorsFromRequest(HttpServletRequest request) {
        Object errors = request.getAttribute(AppConstants.ATTR_FORM_ERRORS);
        if (errors instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) errors;
            return map;
        }
        return null;
    }

}
