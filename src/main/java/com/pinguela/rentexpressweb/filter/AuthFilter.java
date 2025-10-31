package com.pinguela.rentexpressweb.filter;

import java.io.IOException;


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

    private static final String PUBLIC_REGISTER_USER_ENDPOINT = "/app/users/register";

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
        String path = uri.substring(contextPath.length());

        if (requiresAuthentication(path)) {
            HttpSession session = httpRequest.getSession(false);
            Object currentUser = session != null ? session.getAttribute("currentUser") : null;
            if (currentUser == null) {
                httpResponse.sendRedirect(contextPath + "/login?error=auth");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean requiresAuthentication(String path) {
        if (path == null) {
            return false;
        }
        if (isPublicPath(path)) {
            return false;
        }
        return path.startsWith("/private/") || path.startsWith("/app/");
    }

    private boolean isPublicPath(String path) {
        if (path.isEmpty()) {
            return true;
        }
        if (PUBLIC_REGISTER_USER_ENDPOINT.equals(path) || path.startsWith(PUBLIC_REGISTER_USER_ENDPOINT)) {
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        // No resources to release
    }
}
