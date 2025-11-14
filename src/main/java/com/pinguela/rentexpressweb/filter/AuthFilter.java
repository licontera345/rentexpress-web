package com.pinguela.rentexpressweb.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.UserDTO;
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

/*
 * Filtro de seguridad que protege las zonas privadas de la aplicación
 * validando si el usuario o el empleado tiene sesión abierta antes de
 * permitir el acceso.
 */
public class AuthFilter implements Filter {

        private static final List<String> PUBLIC_PATH_PREFIXES = Arrays.asList("/public/", "/css/", "/js/", "/img/",
                        "/i18n/", "/uploads/", "/common/");

        private static final Set<String> PUBLIC_EXACT_PATHS = new HashSet<>(
                        Arrays.asList("", "/", "/public", Views.PUBLIC_INDEX, Views.PUBLIC_LOGIN,
                                        Views.PUBLIC_REGISTER_USER, Views.PUBLIC_VEHICLE_LIST, Views.PUBLIC_VEHICLE_DETAIL,
                                        AppConstants.ROUTE_PUBLIC_LOGIN, Views.SERVLET_LANGUAGE, "/favicon.ico"));

        private static final Set<String> SHARED_PRIVATE_PATHS = new HashSet<>(
                        Arrays.asList(AppConstants.ROUTE_PRIVATE_PROFILE));

        @Override
        /*
         * No necesita recursos adicionales al inicializarse porque su
         * configuración se realiza a través de constantes.
         */
        public void init(FilterConfig filterConfig) throws ServletException {
                // No requiere inicialización
        }

        @Override
        /*
         * Evalúa cada petición entrante y redirige a la página de login si no hay
         * sesión válida o el perfil no dispone de permisos suficientes.
         */
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                        throws IOException, ServletException {

                if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
                        chain.doFilter(request, response);
                        return;
                }

                HttpServletRequest req = (HttpServletRequest) request;
                HttpServletResponse res = (HttpServletResponse) response;
                String path = req.getRequestURI().substring(req.getContextPath().length());

                if (isPublicPath(path)) {
                        chain.doFilter(request, response);
                        return;
                }

                HttpSession session = req.getSession(false);
                UserDTO user = (session != null) ? (UserDTO) session.getAttribute(AppConstants.ATTR_CURRENT_USER) : null;
                EmployeeDTO employee = (session != null)
                                ? (EmployeeDTO) session.getAttribute(AppConstants.ATTR_CURRENT_EMPLOYEE)
                                : null;

                if (path.startsWith("/private/")) {
                        if (employee != null || (user != null && SHARED_PRIVATE_PATHS.contains(path))) {
                                applyNoCache(res);
                                chain.doFilter(request, response);
                                return;
                        }
                        storeTarget(req, session, path);
                        res.sendRedirect(req.getContextPath() + AppConstants.ROUTE_PUBLIC_LOGIN);
                        return;
                }

                if (user != null || employee != null) {
                        chain.doFilter(request, response);
                        return;
                }

                res.sendRedirect(req.getContextPath() + AppConstants.ROUTE_PUBLIC_LOGIN);
        }

        @Override
        /*
         * No necesita liberar recursos específicos al destruirse porque no
         * mantiene estado interno.
         */
        public void destroy() {
                // No requiere limpieza
        }

        /*
         * Determina si la ruta solicitada debe quedar fuera del control del filtro
         * porque pertenece a recursos públicos (estáticos o servlets públicos).
         */
        private boolean isPublicPath(String path) {
                if (path == null || path.isEmpty()) {
                        return true;
                }

                if (PUBLIC_EXACT_PATHS.contains(path)) {
                        return true;
                }

                int i;
                for (i = 0; i < PUBLIC_PATH_PREFIXES.size(); i++) {
                        String prefix = PUBLIC_PATH_PREFIXES.get(i);
                        if (path.startsWith(prefix)) {
                                return true;
                        }
                }
                return false;
        }

        /*
         * Guarda la URL solicitada para devolver al usuario al destino original
         * después de autenticarse correctamente.
         */
        private void storeTarget(HttpServletRequest request, HttpSession session, String path) {
                if (!"GET".equalsIgnoreCase(request.getMethod())) {
                        return;
                }
                if (session == null) {
                        session = request.getSession(true);
                }
                StringBuilder target = new StringBuilder(path);
                String query = request.getQueryString();
                if (query != null && !query.isEmpty()) {
                        target.append('?').append(query);
                }
                session.setAttribute(AppConstants.ATTR_REDIRECT_TARGET, target.toString());
        }

        /*
         * Asegura que las páginas privadas no queden cacheadas en el navegador
         * cumpliendo las normas del profesor.
         */
        private void applyNoCache(HttpServletResponse res) {
                res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                res.setHeader("Pragma", "no-cache");
                res.setDateHeader("Expires", 0);
        }
}
