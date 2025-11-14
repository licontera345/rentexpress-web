package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/index")
public class PublicHomeServlet extends BasePrivateServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(PublicHomeServlet.class);

    private final VehicleService vehicleService = new VehicleServiceImpl();

    @Override
    /*
     * Facilita el logger específico para integrarse con la clase base.
     */
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    /*
     * Atiende la carga de la portada pública estableciendo la codificación y
     * reenviando a la vista correspondiente.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configureEncoding(request, response);

        try {
            List<VehicleDTO> vehicles = vehicleService.findAll();
            if (vehicles == null) {
                vehicles = Collections.emptyList();
            }
            request.setAttribute("featuredVehicles", vehicles);
        } catch (RentexpresException ex) {
            LOGGER.error("Error loading featured vehicles for public home", ex);
            request.setAttribute("featuredVehicles", Collections.emptyList());
        }

        forward(request, response, Views.PUBLIC_INDEX);
    }

    @Override
    /*
     * Redirige las peticiones POST de la portada a la misma ruta para evitar
     * duplicados.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/public/index");
    }
}
