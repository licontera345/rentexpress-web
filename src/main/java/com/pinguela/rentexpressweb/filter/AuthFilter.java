package com.pinguela.rentexpressweb.filter;

import java.io.IOException;

import com.pinguela.rentexpressweb.constants.AppConstants;
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
        String path = httpRequest.getRequestURI().substring(contextPath.length());

        if (requiresAuthentication(path)) {
            HttpSession session = httpRequest.getSession(false);
            Object currentUser = session != null ? session.getAttribute(AppConstants.ATTR_CURRENT_USER) : null;
            if (currentUser == null) {
                httpResponse.sendRedirect(contextPath + Views.PUBLIC_LOGIN);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean requiresAuthentication(String path) {
        if (path == null) {
            return false;
        }
        return path.startsWith("/private/") || path.startsWith("/app/");
    }

    @Override
    public void destroy() {
        // No resources to release
    }
}
