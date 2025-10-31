package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.service.user.UserProfileService;
import com.pinguela.rentexpressweb.util.ActivityEntry;
import com.pinguela.rentexpressweb.util.ControllerUtils;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.UserActivityTracker;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Página de inicio privada con resumen del perfil y actividad reciente.
 */
@WebServlet("/app/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserProfileService profileService = new UserProfileService();

    public HomeServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.home.sessionRequired"));
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }

        ControllerUtils.disableCaching(response);
        ControllerUtils.exposeFlashMessages(request);

        Map<String, String> profile = profileService.getOrCreateProfile(request, currentUser.toString());
        String displayName = resolveDisplayName(profile, currentUser.toString());
        List<ActivityEntry> activities = UserActivityTracker.getRecentActivities(request);

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                MessageResolver.getMessage(request, "home.dashboard.pageTitle"));
        request.setAttribute("profile", profile);
        request.setAttribute("greetingName", displayName);
        request.setAttribute("activityEntries", activities);
        request.setAttribute("profileRoleKey", resolveRoleMessageKey(profile.get(UserProfileService.KEY_ROLE)));

        request.getRequestDispatcher(Views.PRIVATE_USER_HOME).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String resolveDisplayName(Map<String, String> profile, String fallback) {
        if (profile != null) {
            String name = profile.get(UserProfileService.KEY_FULL_NAME);
            if (!ControllerUtils.isBlank(name)) {
                return name;
            }
        }
        if (fallback == null) {
            return "";
        }
        String trimmed = fallback.trim();
        int atIndex = trimmed.indexOf('@');
        if (atIndex > 0) {
            return trimmed.substring(0, atIndex);
        }
        return trimmed;
    }

    private String resolveRoleMessageKey(String role) {
        if (role == null) {
            return "home.dashboard.role.client";
        }
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        if ("EMPLOYEE".equals(normalized)) {
            return "home.dashboard.role.employee";
        }
        return "home.dashboard.role.client";
    }
}
