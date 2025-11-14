package com.pinguela.rentexpressweb.filter;

import java.io.IOException;
import java.util.Locale;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

/**
 * Filtro global de codificación UTF-8.
 * Garantiza que todas las peticiones y respuestas usen el mismo charset,
 * evitando problemas con tildes y caracteres especiales.
 */
@WebFilter("/*")
public class EncodingFilter implements Filter {

    private static final String UTF8 = "UTF-8";

    @Override
    /*
     * Asegura que tanto la petición como la respuesta utilicen UTF-8 y añade el
     * charset al content-type cuando el cliente no lo especifica.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!UTF8.equalsIgnoreCase(request.getCharacterEncoding())) {
            request.setCharacterEncoding(UTF8);
        }

        if (!UTF8.equalsIgnoreCase(response.getCharacterEncoding())) {
            response.setCharacterEncoding(UTF8);
        }

        chain.doFilter(request, response);

        String contentType = response.getContentType();
        if (contentType != null && contentType.startsWith("text/")
                && !contentType.toLowerCase(Locale.ROOT).contains("charset")) {
            response.setContentType(contentType + "; charset=" + UTF8);
        }
    }

    @Override
    /*
     * No se requiere configuración adicional porque el filtro opera con valores
     * constantes.
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        // no necesita configuración
    }

    @Override
    /*
     * No realiza acciones de limpieza ya que no mantiene recursos abiertos.
     */
    public void destroy() {
        // nada que limpiar
    }
}
