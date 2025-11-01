package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.math.BigDecimal;
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
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/vehicles")
public class PublicVehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PublicVehicleServlet.class);

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

        VehicleCriteria criteria = buildCriteria(request);

        try {
            Results<VehicleDTO> results = vehicleService.findByCriteria(criteria);
            List<VehicleDTO> vehicles = results != null && results.getResults() != null ? results.getResults()
                    : Collections.emptyList();

            request.setAttribute(VehicleConstants.ATTR_VEHICLES, vehicles);
            request.setAttribute(VehicleConstants.ATTR_RESULTS, results);
            request.setAttribute(VehicleConstants.ATTR_TOTAL_RESULTS,
                    results != null ? results.getTotalRecords() : Integer.valueOf(vehicles.size()));
        } catch (RentexpresException ex) {
            LOGGER.error("Error retrieving vehicles by criteria", ex);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
            request.setAttribute(VehicleConstants.ATTR_VEHICLES, Collections.emptyList());
        }

        request.setAttribute("search", request.getParameter(VehicleConstants.PARAM_SEARCH));
        request.setAttribute("categoryId", request.getParameter(VehicleConstants.PARAM_CATEGORY));
        request.setAttribute("status", request.getParameter(VehicleConstants.PARAM_STATUS));
        request.setAttribute("priceMin", request.getParameter(VehicleConstants.PARAM_MIN_PRICE));
        request.setAttribute("priceMax", request.getParameter(VehicleConstants.PARAM_MAX_PRICE));

        request.getRequestDispatcher(Views.PUBLIC_VEHICLE_LIST).forward(request, response);
    }

    private VehicleCriteria buildCriteria(HttpServletRequest request) {
        VehicleCriteria criteria = new VehicleCriteria();
        String search = trimToNull(request.getParameter(VehicleConstants.PARAM_SEARCH));
        if (search != null) {
            criteria.setBrand(search);
            criteria.setModel(search);
        }
        criteria.setCategoryId(parseInteger(request.getParameter(VehicleConstants.PARAM_CATEGORY)));
        criteria.setVehicleStatusId(parseInteger(request.getParameter(VehicleConstants.PARAM_STATUS)));
        criteria.setDailyPriceMin(parseBigDecimal(request.getParameter(VehicleConstants.PARAM_MIN_PRICE)));
        criteria.setDailyPriceMax(parseBigDecimal(request.getParameter(VehicleConstants.PARAM_MAX_PRICE)));
        criteria.setPageNumber(parseInteger(request.getParameter(VehicleConstants.PARAM_PAGE)));
        criteria.setPageSize(parseInteger(request.getParameter(VehicleConstants.PARAM_PAGE_SIZE)));
        criteria.normalize();
        return criteria;
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

    private BigDecimal parseBigDecimal(String value) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            return new BigDecimal(normalized);
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
