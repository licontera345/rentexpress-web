package com.pinguela.rentexpressweb.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * Utilidad para crear, consultar y eliminar cookies siguiendo las pautas de
 * seguridad del proyecto (HttpOnly y Secure cuando aplica).
 */
public final class CookieManager {

    private static final int SECONDS_PER_DAY = 24 * 60 * 60;

    private CookieManager() {
    }

    /*
     * Crea una cookie persistente respetando el contexto de la aplicación y las
     * banderas de seguridad básicas.
     */
    public static void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value,
            int days) {
        if (response == null || name == null || value == null) {
            return;
        }
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(resolvePath(request));
        cookie.setMaxAge(days * SECONDS_PER_DAY);
        cookie.setHttpOnly(true);
        cookie.setSecure(request != null && request.isSecure());
        response.addCookie(cookie);
    }

    /*
     * Sobrecarga que permite crear cookies cuando no es necesario utilizar el
     * request, reutilizando la lógica principal.
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int days) {
        addCookie(null, response, name, value, days);
    }

    /*
     * Recupera una cookie concreta asociada a la petición si está presente.
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            if (cookie != null && name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    /*
     * Elimina una cookie fijando su edad a cero y replicando la ruta utilizada al
     * crearla.
     */
    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        if (response == null || name == null) {
            return;
        }
        Cookie cookie = new Cookie(name, "");
        cookie.setPath(resolvePath(request));
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(request != null && request.isSecure());
        response.addCookie(cookie);
    }

    /*
     * Variante simplificada para borrar cookies cuando solo se dispone de la
     * respuesta HTTP.
     */
    public static void removeCookie(HttpServletResponse response, String name) {
        removeCookie(null, response, name);
    }

    /*
     * Determina el path correcto de la cookie para asegurar que el navegador la
     * envía en peticiones posteriores.
     */
    private static String resolvePath(HttpServletRequest request) {
        if (request == null) {
            return "/";
        }
        String contextPath = request.getContextPath();
        return contextPath == null || contextPath.isEmpty() ? "/" : contextPath;
    }
}
