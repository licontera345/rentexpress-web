package com.pinguela.rentexpressweb.util;

import java.util.Collections;
import java.util.List;

import com.pinguela.rentexpres.model.Results;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utilidades comunes para trabajar con la paginación en los servlets. La
 * lógica de negocio (total de elementos y consultas) reside en el middleware;
 * aquí únicamente volcamos los datos en la request para que las JSP los
 * muestren.
 */
public final class PaginationUtils {

    private static final int DEFAULT_PAGE = 1;

    private PaginationUtils() {
    }

    /**
     * Resuelve la página solicitada en la petición. Si el parámetro está ausente
     * o contiene un valor no numérico se devuelve siempre la página 1.
     *
     * @param req petición HTTP de origen
     * @return número de página válido (>=1)
     */
    public static int resolvePage(HttpServletRequest req) {
        try {
            String p = req.getParameter("page");
            return (p == null || p.isEmpty()) ? DEFAULT_PAGE : Integer.parseInt(p);
        } catch (NumberFormatException e) {
            return DEFAULT_PAGE;
        }
    }

    /**
     * Obtiene el tamaño de página deseado a partir del parámetro indicado. Si el
     * valor no existe o no es numérico se devuelve el tamaño por defecto
     * especificado.
     *
     * @param req         petición HTTP de origen
     * @param paramName   nombre del parámetro que contiene el tamaño de página
     * @param defaultSize tamaño de página a utilizar si la petición no lo indica
     * @return tamaño de página válido (>=1)
     */
    public static int resolvePageSize(HttpServletRequest req, String paramName, int defaultSize) {
        if (paramName == null || paramName.isEmpty()) {
            return defaultSize;
        }
        try {
            String size = req.getParameter(paramName);
            return (size == null || size.isEmpty()) ? defaultSize : Integer.parseInt(size);
        } catch (NumberFormatException e) {
            return defaultSize;
        }
    }

    /**
     * Copia los resultados y metadatos de paginación en la request para que la
     * vista pueda generar la navegación.
     *
     * @param <T>     tipo de dato contenido en la página
     * @param req     petición HTTP
     * @param rs      resultados paginados provenientes del middleware
     * @param page    página solicitada
     * @param pageSize tamaño de página utilizado en la consulta
     * @param attr    nombre del atributo donde se guardará la lista
     */
    public static <T> void apply(HttpServletRequest req, Results<T> rs, int page, int pageSize, String attr) {
        List<T> data = (rs != null && rs.getResults() != null) ? rs.getResults() : Collections.emptyList();
        req.setAttribute(attr, data);

        int total = (rs != null && rs.getTotalRecords() != null) ? rs.getTotalRecords() : 0;
        req.setAttribute("totalResults", total);

        int resolvedPage = page;
        if (rs != null && rs.getPageNumber() != null && rs.getPageNumber().intValue() > 0) {
            resolvedPage = rs.getPageNumber().intValue();
        }
        req.setAttribute("currentPage", resolvedPage);

        int resolvedSize = pageSize;
        if (rs != null && rs.getPageSize() != null && rs.getPageSize().intValue() > 0) {
            resolvedSize = rs.getPageSize().intValue();
        }
        req.setAttribute("pageSize", resolvedSize);

        int totalPages = 0;
        if (rs != null && rs.getTotalPages() != null) {
            totalPages = rs.getTotalPages();
        } else if (resolvedSize > 0) {
            totalPages = (int) Math.ceil((double) total / resolvedSize);
        }
        req.setAttribute("totalPages", totalPages);
    }
}
