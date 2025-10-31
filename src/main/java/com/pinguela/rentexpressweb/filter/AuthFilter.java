package com.pinguela.rentexpressweb.filter;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.web.security.RememberMeCookies;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Filtro de autorización que controla el acceso a las rutas privadas de la aplicación.
 */
public class AuthFilter implements Filter {

    private static final Logger LOGGER = LogManager.getLogger(AuthFilter.class);

    private static final String[] PUBLIC_PATH_PREFIXES = new String[] { "/public/", "/css/", "/common/", "/resources/",
            "/images/", "/js/", "/favicon", "/private/security/" };

    private static final String[] PUBLIC_PATHS = new String[] { "/", "", SecurityConstants.LOGIN_ENDPOINT,
            "/app/auth/verify-2fa", "/app/auth/logout", "/app/users/register", "/app/password/forgot",
            "/app/password/reset", "/app/password/verify-reset", "/app/settings/language", "/public/home",
            "/app/images/view" };

    private static final String[] AUTH_PATHS = new String[] { "/app/home", "/app/users/private",
            "/app/users/profile", "/app/reservations/private", "/app/rentals/private", "/app/images/download",
            "/app/images/upload", "/app/employees/private", "/app/employees/profile", "/app/employees/register",
            "/app/vehicles/manage" };

    private static final String[] AUTH_PREFIXES = new String[] { "/private/user/", "/private/rental/",
            "/private/reservation/", "/private/employee/", "/private/vehicle/" };

    private static final String MESSAGE_KEY_LOGIN_REQUIRED = "error.auth.loginRequired";
    private static final String MESSAGE_KEY_FORBIDDEN = "error.auth.accessDenied";

    public AuthFilter() {
        super();
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        // No se requiere inicialización específica.
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        RememberMeCookies.syncSession(httpRequest);

        String path = extractPath(httpRequest);
        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        boolean authenticated = isAuthenticated(httpRequest);

        if (requiresEmployee(path)) {
            if (!authenticated) {
                redirectToLogin(httpRequest, httpResponse);
                return;
            }
            if (!isEmployee(httpRequest)) {
                Object user = SessionManager.getAttribute(httpRequest, AppConstants.ATTR_CURRENT_USER);
                LOGGER.warn("Acceso denegado a {} para {}", path, user);
                denyAccess(httpRequest, httpResponse);
                return;
            }
        } else if (requiresAuthentication(path) && !authenticated) {
            redirectToLogin(httpRequest, httpResponse);
            return;
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
        // No se requiere limpieza especial.
    }

    private String extractPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null) {
            return "";
        }
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    private boolean isPublic(String path) {
        if (path == null || path.isEmpty()) {
            return true;
        }
        for (int i = 0; i < PUBLIC_PATHS.length; i++) {
            if (PUBLIC_PATHS[i].equals(path)) {
                return true;
            }
        }
        for (int i = 0; i < PUBLIC_PATH_PREFIXES.length; i++) {
            if (path.startsWith(PUBLIC_PATH_PREFIXES[i])) {
                return true;
            }
        }
        if (path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg")
                || path.endsWith(".jpeg") || path.endsWith(".gif") || path.endsWith(".svg") || path.endsWith(".ico")) {
            return true;
        }
        return false;
    }

    private boolean requiresAuthentication(String path) {
        for (int i = 0; i < AUTH_PATHS.length; i++) {
            if (AUTH_PATHS[i].equals(path)) {
                return true;
            }
        }
        for (int i = 0; i < AUTH_PREFIXES.length; i++) {
            if (path.startsWith(AUTH_PREFIXES[i])) {
                return true;
            }
        }
        return path.startsWith("/app/");
    }

    private boolean requiresEmployee(String path) {
        if (path.startsWith("/private/employee/") || path.startsWith("/private/vehicle/")) {
            return true;
        }
        if ("/app/images/upload".equals(path) || "/app/employees/private".equals(path)
                || "/app/employees/profile".equals(path) || "/app/employees/register".equals(path)
                || "/app/vehicles/manage".equals(path)) {
            return true;
        }
        return false;
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        return currentUser != null;
    }

    private boolean isEmployee(HttpServletRequest request) {
        Object currentEmployee = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        return currentEmployee != null;
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String message = MessageResolver.getMessage(request, MESSAGE_KEY_LOGIN_REQUIRED);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR, message);
        response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
    }

    private void denyAccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String message = MessageResolver.getMessage(request, MESSAGE_KEY_FORBIDDEN);
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR, message);
        response.sendRedirect(request.getContextPath() + SecurityConstants.HOME_ENDPOINT);
    }
}
