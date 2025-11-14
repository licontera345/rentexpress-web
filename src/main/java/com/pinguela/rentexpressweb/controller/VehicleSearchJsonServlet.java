package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.model.VehicleCriteria;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpressweb.util.RequestHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Endpoint JSON ligero para autocompletar marcas/modelos en el catálogo
 * público. No introduce lógica de negocio adicional; simplemente delega en el
 * servicio del middleware y serializa el resultado.
 */
@WebServlet("/public/vehicles/search")
public class VehicleSearchJsonServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(VehicleSearchJsonServlet.class);

    private final VehicleService vehicleService;
    private final Gson gson;

    /*
     * Configura el servlet con las implementaciones por defecto del servicio de
     * vehículos y del serializador JSON.
     */
    public VehicleSearchJsonServlet() {
        this(new VehicleServiceImpl(), new Gson());
    }

    /*
     * Permite inyectar dependencias personalizadas (como dobles en tests) para el
     * servicio y el conversor JSON.
     */
    VehicleSearchJsonServlet(VehicleService vehicleService, Gson gson) {
        this.vehicleService = vehicleService;
        this.gson = gson;
    }

    @Override
    /*
     * Responde a peticiones GET construyendo un criterio de búsqueda por marca,
     * delegando en el servicio y devolviendo los resultados en formato JSON.
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = RequestHelper.param(req, "q");
        VehicleCriteria criteria = new VehicleCriteria();
        criteria.setBrand(query);
        criteria.setPageNumber(1);
        criteria.setPageSize(10);

        List<VehicleDTO> page = Collections.emptyList();
        try {
            Results<VehicleDTO> results = vehicleService.findByCriteria(criteria);
            if (results != null && results.getResults() != null) {
                page = results.getResults();
            }
        } catch (RentexpresException ex) {
            LOGGER.error("Error buscando vehículos para autocompletado", ex);
        }

        resp.setContentType("application/json; charset=UTF-8");
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(gson.toJson(page));
        }
    }
}
