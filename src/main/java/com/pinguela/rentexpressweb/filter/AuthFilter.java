package com.pinguela.rentexpressweb.filter;

import java.io.IOException;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization required
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String uri = httpRequest.getRequestURI();
        String relativeUri = uri.substring(contextPath.length());

        boolean isPublicPath = relativeUri.contains("/login")
                || relativeUri.contains("/users/register")
                || relativeUri.contains("/employees/register")
                || relativeUri.contains("/public/")
                || relativeUri.contains("/app/settings/language");

        boolean requiresAuth = relativeUri.startsWith(AppConstants.PATH_APP_ROOT)
                || relativeUri.startsWith(AppConstants.PATH_PRIVATE_ROOT);

        if (!isPublicPath && requiresAuth) {
            HttpSession session = httpRequest.getSession(false);
            Object currentUser = SessionManager.get(httpRequest, AppConstants.ATTR_CURRENT_USER);
            if (session == null || currentUser == null) {
                httpResponse.sendRedirect(contextPath + Views.PUBLIC_LOGIN);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No resources to release
    }
}
