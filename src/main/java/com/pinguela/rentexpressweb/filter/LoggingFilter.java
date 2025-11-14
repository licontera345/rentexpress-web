package com.pinguela.rentexpressweb.filter;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro encargado de registrar cada petición HTTP con su método, URL y código
 * de estado. No modifica la lógica de la aplicación, solo deja trazas para
 * depuración.
 */
@WebFilter("/*")
public class LoggingFilter implements Filter {

	private static final Logger LOGGER = LogManager.getLogger(LoggingFilter.class);

        @Override
        /*
         * No precisa configuración porque su comportamiento depende únicamente
         * del logger de aplicación.
         */
        public void init(FilterConfig filterConfig) throws ServletException {
                // No requiere configuración inicial
        }

        @Override
        /*
         * Registra la petición y su duración manteniendo intacto el flujo de la
         * aplicación.
         */
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                        throws IOException, ServletException {

		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		long start = System.currentTimeMillis();
		int statusCode;

		try {
			chain.doFilter(request, response);
			statusCode = res.getStatus();
		} finally {
			long duration = System.currentTimeMillis() - start;
			statusCode = res.getStatus();

			// Log controlado, formato estándar del profesor
			LOGGER.info("{} [{}] {} -> {} ({} ms)", LocalDateTime.now(), req.getMethod(),
					req.getRequestURI().substring(req.getContextPath().length()), statusCode, duration);
		}
	}

        @Override
        /*
         * No realiza ninguna acción al destruirse porque no mantiene estado.
         */
        public void destroy() {
                // Sin recursos que liberar
        }
}
