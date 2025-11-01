package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.model.VehicleCriteria;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/vehicles")
public class VehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(VehicleServlet.class);

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
        if (SessionManager.get(request, AppConstants.ATTR_CURRENT_EMPLOYEE) == null
                && SessionManager.get(request, AppConstants.ATTR_CURRENT_USER) == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }

        VehicleCriteria criteria = new VehicleCriteria();
        criteria.setVehicleStatusId(parseInteger(request.getParameter(VehicleConstants.PARAM_STATUS)));
        criteria.setCategoryId(parseInteger(request.getParameter(VehicleConstants.PARAM_CATEGORY)));
        criteria.setBrand(trimToNull(request.getParameter(VehicleConstants.PARAM_BRAND)));
        criteria.setModel(trimToNull(request.getParameter(VehicleConstants.PARAM_MODEL)));

        try {
            Results<VehicleDTO> results = vehicleService.findByCriteria(criteria);
            List<VehicleDTO> vehicles = results != null && results.getResults() != null ? results.getResults()
                    : Collections.emptyList();
            request.setAttribute(VehicleConstants.ATTR_VEHICLES, vehicles);
            request.setAttribute(VehicleConstants.ATTR_RESULTS, results);
        } catch (RentexpresException ex) {
            LOGGER.error("Error retrieving internal vehicle listing", ex);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
            request.setAttribute(VehicleConstants.ATTR_VEHICLES, Collections.emptyList());
        }

        request.getRequestDispatcher(Views.PRIVATE_VEHICLE_LIST).forward(request, response);
    }

    private Integer parseInteger(String value) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            return Integer.valueOf(normalized);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
