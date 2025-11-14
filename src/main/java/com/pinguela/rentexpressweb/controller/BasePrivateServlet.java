package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.util.RequestHelper;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class BasePrivateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /*
     * Obliga a cada servlet hijo a proporcionar su instancia de logger para poder
     * registrar trazas homogéneas en todo el módulo privado.
     */
    protected abstract Logger getLogger();

    /*
     * Permite a los servlets hijos aplicar configuraciones adicionales de
     * codificación cuando sea necesario, manteniendo la compatibilidad con la
     * jerarquía heredada.
     */
    protected void configureEncoding(HttpServletRequest request, HttpServletResponse response) {
        // La codificación UTF-8 se aplica de forma global a través de EncodingFilter.
        // Este método se mantiene por compatibilidad con la jerarquía de servlets.
    }

    /*
     * Verifica que exista un usuario o empleado autenticado en sesión y, en caso
     * contrario, redirige a la pantalla de login para proteger los recursos
     * privados.
     */
    protected boolean ensureAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionManager.get(request, AppConstants.ATTR_CURRENT_EMPLOYEE) != null
                || SessionManager.get(request, AppConstants.ATTR_CURRENT_USER) != null) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
        return false;
    }

    /*
     * Determina si la petición actual se ha realizado mediante el verbo POST para
     * facilitar decisiones de flujo en los servlets concretos.
     */
    protected boolean isPost(HttpServletRequest request) {
        return request != null && "POST".equalsIgnoreCase(request.getMethod());
    }

    /*
     * Recupera un parámetro de la petición aplicando las utilidades comunes de
     * normalización y limpieza.
     */
    protected String param(HttpServletRequest request, String name) {
        return RequestHelper.param(request, name);
    }

    /*
     * Realiza un forward hacia la vista indicada delegando en el
     * RequestDispatcher del contenedor.
     */
    protected void forward(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(view);
        dispatcher.forward(request, response);
    }

    /*
     * Construye una redirección incluyendo, si procede, un parámetro marcador que
     * permita a la vista mostrar mensajes de resultado tras operaciones POST.
     */
    protected void redirectWithFlag(HttpServletRequest request, HttpServletResponse response, String path, String flagParam)
            throws IOException {
        StringBuilder target = new StringBuilder(request.getContextPath()).append(path);
        if (flagParam != null && !flagParam.isEmpty()) {
            target.append('?').append(flagParam).append('=').append(AppConstants.VALUE_FLAG_TRUE);
        }
        response.sendRedirect(target.toString());
    }

    /*
     * Traslada a la request los mensajes flash almacenados en sesión para que la
     * vista pueda mostrarlos una única vez y después limpiarlos.
     */
    protected void transferFlashAttributes(HttpServletRequest request) {
        Object success = SessionManager.get(request, AppConstants.ATTR_FLASH_SUCCESS);
        if (success != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_SUCCESS, success);
            SessionManager.remove(request, AppConstants.ATTR_FLASH_SUCCESS);
        }

        Object error = SessionManager.get(request, AppConstants.ATTR_FLASH_ERROR);
        if (error != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, error);
            SessionManager.remove(request, AppConstants.ATTR_FLASH_ERROR);
        }
    }

    /*
     * Obtiene el idioma preferido de la petición aprovechando la lógica común de
     * detección de locale.
     */
    protected String resolveLanguage(HttpServletRequest request) {
        return RequestHelper.resolveLanguage(request);
    }
}
