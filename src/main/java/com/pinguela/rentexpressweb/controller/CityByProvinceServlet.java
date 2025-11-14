package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.CityDTO;
import com.pinguela.rentexpres.service.CityService;
import com.pinguela.rentexpres.service.impl.CityServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/cities/by-province")
public class CityByProvinceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(CityByProvinceServlet.class);

    private final CityService cityService;
    private final Gson gson;

    /*
     * Inicializa el servlet con las implementaciones por defecto del servicio de
     * ciudades y del conversor JSON para uso en producción.
     */
    public CityByProvinceServlet() {
        this(new CityServiceImpl(), new Gson());
    }

    /*
     * Permite inyectar dependencias personalizadas (útil en pruebas) para resolver
     * las ciudades por provincia y serializar la respuesta JSON.
     */
    CityByProvinceServlet(CityService cityService, Gson gson) {
        this.cityService = cityService;
        this.gson = gson;
    }

    @Override
    /*
     * Atiende las peticiones GET convirtiendo el identificador de provincia en un
     * listado de ciudades y devolviendo la información en formato JSON consumible
     * por la capa de presentación.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String provinceIdParam = request.getParameter(AppConstants.PARAM_PROVINCE_ID);
        List<CityDTO> cities = Collections.emptyList();

        if (provinceIdParam != null && !provinceIdParam.isEmpty()) {
            try {
                Integer provinceId = Integer.valueOf(provinceIdParam);
                cities = cityService.findByProvinceId(provinceId);
                if (cities == null) {
                    cities = Collections.emptyList();
                }
            } catch (NumberFormatException ex) {
                LOGGER.warn("Invalid provinceId received: {}", provinceIdParam, ex);
            } catch (RentexpresException ex) {
                LOGGER.error("Error retrieving cities for province {}", provinceIdParam, ex);
            }
        }

        List<CityPayload> payload = new ArrayList<>();
        for (CityDTO city : cities) {
            if (city == null) {
                continue;
            }
            payload.add(new CityPayload(city.getId(), city.getCityName()));
        }

        response.setContentType("application/json; charset=UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(gson.toJson(payload));
        }
    }

    private static final class CityPayload {
        private final Integer id;
        private final String name;

        /*
         * Envuelve los datos mínimos de una ciudad para serializarlos a JSON sin
         * recurrir a estructuras basadas en mapas.
         */
        private CityPayload(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
