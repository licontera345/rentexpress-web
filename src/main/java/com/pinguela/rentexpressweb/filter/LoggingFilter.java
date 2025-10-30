package com.pinguela.rentexpressweb.filter;

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
 * Filtro sencillo para registrar las peticiones recibidas y su tiempo de respuesta.
 */
public class LoggingFilter implements Filter {

    private static final Logger LOGGER = LogManager.getLogger(LoggingFilter.class);
    private static final String TEMPLATE_REQUEST = "Petición {} {}";
    private static final String TEMPLATE_RESPONSE = "Respuesta {} {} -> {} en {} ms";
    private static final String TEMPLATE_RESPONSE_NO_STATUS = "Respuesta {} {} en {} ms";

    public LoggingFilter() {
        super();
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        // No se requiere inicialización específica.
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = null;
        if (response instanceof HttpServletResponse) {
            httpResponse = (HttpServletResponse) response;
        }

        String method = httpRequest.getMethod();
        String path = buildRequestPath(httpRequest);
        long start = System.currentTimeMillis();
        LOGGER.info(TEMPLATE_REQUEST, method, path);
        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            if (httpResponse != null) {
                LOGGER.info(TEMPLATE_RESPONSE, method, path, Integer.valueOf(httpResponse.getStatus()),
                        Long.valueOf(duration));
            } else {
                LOGGER.info(TEMPLATE_RESPONSE_NO_STATUS, method, path, Long.valueOf(duration));
            }
        }
    }

    public void destroy() {
        // No se requiere limpieza especial.
    }

    private String buildRequestPath(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        String uri = request.getRequestURI();
        if (uri != null) {
            builder.append(uri);
        }
        String query = request.getQueryString();
        if (query != null && !query.isEmpty()) {
            builder.append('?').append(query);
        }
        return builder.toString();
    }
}
