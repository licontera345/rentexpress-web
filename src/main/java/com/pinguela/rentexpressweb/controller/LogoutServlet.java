package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Cierra la sesión actual invalidando los datos almacenados.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(LogoutServlet.class);

    @Override
    /*
     * Atiende las solicitudes GET para cerrar la sesión actual y redirigir a la
     * portada pública.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        performLogout(request, response);
    }

    @Override
    /*
     * Gestiona las peticiones POST del formulario de cierre de sesión delegando
     * en la lógica común de logout.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        performLogout(request, response);
    }

    /*
     * Invalida la sesión y redirige al usuario a la página de inicio tras registrar
     * la operación en el log.
     */
    private void performLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionManager.invalidate(request);
        LOGGER.info("Sesión cerrada correctamente");
        response.sendRedirect(request.getContextPath() + Views.PUBLIC_INDEX);
    }
}
