package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
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
public class PrivateDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String KEY_ID = "id";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_AVATAR = "avatarPath";

    private static final String DEFAULT_FULL_NAME = "Cliente Demo";
    private static final String DEFAULT_PHONE = "+34 600 000 000";
    private static final String DEFAULT_ROLE = "CLIENT";

    public PrivateDashboardServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireUser(request, response,
                MessageResolver.getMessage(request, "error.home.sessionRequired"))) {
            return;
        }

        disableCaching(response);
        copyFlashMessages(request);

        Object currentUser = SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        Map<String, String> profile = ensureProfile(request, currentUser.toString());
        String displayName = resolveDisplayName(profile, currentUser.toString());

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE,
                MessageResolver.getMessage(request, "home.dashboard.pageTitle"));
        request.setAttribute("profile", profile);
        request.setAttribute("greetingName", displayName);
        request.setAttribute("activityEntries", Collections.emptyList());
        request.setAttribute("profileRoleKey", resolveRoleMessageKey(profile.get(KEY_ROLE)));

        forward(request, response, Views.PRIVATE_USER_HOME);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private Map<String, String> ensureProfile(HttpServletRequest request, String email) {
        @SuppressWarnings("unchecked")
        Map<String, String> profile = (Map<String, String>) SessionManager.get(request,
                UserConstants.ATTR_PROFILE_DATA);
        if (profile == null) {
            profile = new HashMap<String, String>();
            SessionManager.set(request, UserConstants.ATTR_PROFILE_DATA, profile);
        }

        if (!profile.containsKey(KEY_ID) || isBlank(profile.get(KEY_ID))) {
            profile.put(KEY_ID, generateIdentifier(email));
        }
        if (!profile.containsKey(KEY_FULL_NAME) || isBlank(profile.get(KEY_FULL_NAME))) {
            profile.put(KEY_FULL_NAME, DEFAULT_FULL_NAME);
        }
        if (!profile.containsKey(KEY_PHONE)) {
            profile.put(KEY_PHONE, DEFAULT_PHONE);
        }
        if (!profile.containsKey(KEY_EMAIL) || isBlank(profile.get(KEY_EMAIL))) {
            profile.put(KEY_EMAIL, email);
        }
        if (!profile.containsKey(KEY_ROLE) || isBlank(profile.get(KEY_ROLE))) {
            profile.put(KEY_ROLE, DEFAULT_ROLE);
        }
        if (!profile.containsKey(KEY_AVATAR)) {
            profile.put(KEY_AVATAR, "");
        }
        return profile;
    }

    private String resolveDisplayName(Map<String, String> profile, String fallback) {
        if (profile != null) {
            String name = profile.get(KEY_FULL_NAME);
            if (!isBlank(name)) {
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

    private String generateIdentifier(String seed) {
        if (seed == null) {
            return "profile";
        }
        String lower = seed.toLowerCase(Locale.ROOT);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lower.length(); i++) {
            char ch = lower.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')) {
                builder.append(ch);
            }
        }
        if (builder.length() == 0) {
            builder.append("profile");
        }
        return builder.toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean requireUser(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws IOException {
        if (SessionManager.get(request, AppConstants.ATTR_CURRENT_USER) != null) {
            return true;
        }
        if (errorMessage != null && !errorMessage.isEmpty()) {
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR, errorMessage);
        }
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    private void disableCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private void copyFlashMessages(HttpServletRequest request) {
        transferFlashAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        transferFlashAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        transferFlashAttribute(request, AppConstants.ATTR_FLASH_INFO);
    }

    private void transferFlashAttribute(HttpServletRequest request, String name) {
        Object value = SessionManager.get(request, name);
        if (value != null) {
            request.setAttribute(name, value);
            SessionManager.remove(request, name);
        }
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher(view).forward(request, response);
    }
}
