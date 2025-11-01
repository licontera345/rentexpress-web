package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

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

    private transient VehicleService vehicleService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.vehicleService = new VehicleServiceStub();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        VehicleSearchCriteria criteria = buildCriteria(request);
        List<?> vehicles = vehicleService.findByCriteria(criteria);

        request.setAttribute("search", criteria.getSearch());
        request.setAttribute("categoryId", toString(criteria.getCategoryId()));
        request.setAttribute("status", criteria.getStatus());
        request.setAttribute("priceMin", toString(criteria.getPriceMin()));
        request.setAttribute("priceMax", toString(criteria.getPriceMax()));
        request.setAttribute("availableOnly", criteria.isAvailableOnly());
        request.setAttribute(VehicleConstants.ATTR_VEHICLES, vehicles);

        request.getRequestDispatcher(Views.PUBLIC_VEHICLE_LIST).forward(request, response);
    }

    private VehicleSearchCriteria buildCriteria(HttpServletRequest request) {
        VehicleSearchCriteria criteria = new VehicleSearchCriteria();
        criteria.setSearch(trimToNull(request.getParameter(VehicleConstants.PARAM_SEARCH)));
        criteria.setCategoryId(parseLong(request.getParameter(VehicleConstants.PARAM_CATEGORY)));
        criteria.setStatus(trimToNull(request.getParameter(VehicleConstants.PARAM_STATUS)));
        criteria.setPriceMin(parseBigDecimal(request.getParameter(VehicleConstants.PARAM_MIN_PRICE)));
        criteria.setPriceMax(parseBigDecimal(request.getParameter(VehicleConstants.PARAM_MAX_PRICE)));
        criteria.setAvailableOnly(request.getParameter(VehicleConstants.PARAM_ONLY_AVAILABLE) != null);
        return criteria;
    }

    private Long parseLong(String value) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            return Long.valueOf(normalized);
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

    private String toString(Object value) {
        return value == null ? null : value.toString();
    }

    private interface VehicleService {
        List<?> findByCriteria(VehicleSearchCriteria criteria);
    }

    private static final class VehicleServiceStub implements VehicleService {
        @Override
        public List<?> findByCriteria(VehicleSearchCriteria criteria) {
            return Collections.emptyList();
        }
    }

    private static final class VehicleSearchCriteria {
        private String search;
        private Long categoryId;
        private String status;
        private BigDecimal priceMin;
        private BigDecimal priceMax;
        private boolean availableOnly;

        String getSearch() {
            return search;
        }

        void setSearch(String search) {
            this.search = search;
        }

        Long getCategoryId() {
            return categoryId;
        }

        void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        String getStatus() {
            return status;
        }

        void setStatus(String status) {
            this.status = status;
        }

        BigDecimal getPriceMin() {
            return priceMin;
        }

        void setPriceMin(BigDecimal priceMin) {
            this.priceMin = priceMin;
        }

        BigDecimal getPriceMax() {
            return priceMax;
        }

        void setPriceMax(BigDecimal priceMax) {
            this.priceMax = priceMax;
        }

        boolean isAvailableOnly() {
            return availableOnly;
        }

        void setAvailableOnly(boolean availableOnly) {
            this.availableOnly = availableOnly;
        }
    }
}
