package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/vehicles/detail")
public class PublicVehicleDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PublicVehicleDetailServlet.class);

    private transient VehicleService vehicleService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.vehicleService = new VehicleServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Integer vehicleId = parseInteger(request.getParameter(VehicleConstants.PARAM_VEHICLE_ID));
        if (vehicleId == null) {
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, "Vehicle identifier is required");
            request.setAttribute(VehicleConstants.ATTR_SELECTED_VEHICLE, null);
            request.getRequestDispatcher(Views.PUBLIC_VEHICLE_DETAIL).forward(request, response);
            return;
        }

        try {
            VehicleDTO vehicle = vehicleService.findById(vehicleId);
            if (vehicle == null) {
                request.setAttribute(AppConstants.ATTR_FLASH_ERROR, "Vehicle not found");
            }
            request.setAttribute(VehicleConstants.ATTR_SELECTED_VEHICLE, vehicle);
        } catch (RentexpresException ex) {
            LOGGER.error("Unable to load vehicle detail for id {}", vehicleId, ex);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
            request.setAttribute(VehicleConstants.ATTR_SELECTED_VEHICLE, null);
        }
        request.getRequestDispatcher(Views.PUBLIC_VEHICLE_DETAIL).forward(request, response);
    }

    private Integer parseInteger(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
