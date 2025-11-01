package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.model.VehicleCriteria;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.VehicleStatusService;
import com.pinguela.rentexpres.service.impl.VehicleStatusServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpressweb.service.VehicleCatalogService;
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

    private transient VehicleCatalogService vehicleService;
    private transient VehicleStatusService vehicleStatusService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.vehicleService = new VehicleCatalogService();
        this.vehicleStatusService = new VehicleStatusServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String brand = resolveParameter(request, VehicleConstants.PARAM_BRAND);
        String model = resolveParameter(request, VehicleConstants.PARAM_MODEL);
        String search = resolveParameter(request, VehicleConstants.PARAM_SEARCH);

        if (brand == null && search != null) {
            brand = search;
        }
        if (model == null && search != null) {
            model = search;
        }

        Integer categoryId = parseInteger(resolveParameter(request, VehicleConstants.PARAM_CATEGORY));
        Integer statusId = parseInteger(resolveParameter(request, VehicleConstants.PARAM_STATUS,
                VehicleConstants.PARAM_STATUS_ID));
        BigDecimal minPrice = parseBigDecimal(resolveParameter(request, VehicleConstants.PARAM_MIN_PRICE,
                VehicleConstants.PARAM_MIN_PRICE_ALT));
        BigDecimal maxPrice = parseBigDecimal(resolveParameter(request, VehicleConstants.PARAM_MAX_PRICE,
                VehicleConstants.PARAM_MAX_PRICE_ALT));
        boolean onlyAvailable = parseBoolean(resolveParameter(request, VehicleConstants.PARAM_ONLY_AVAILABLE));
        Integer pageParam = parseInteger(resolveParameter(request, VehicleConstants.PARAM_PAGE));
        Integer sizeParam = parseInteger(resolveParameter(request, VehicleConstants.PARAM_PAGE_SIZE,
                VehicleConstants.PARAM_PAGE_SIZE_ALIAS));

        int pageNumber = pageParam != null && pageParam.intValue() > 0 ? pageParam.intValue() : 1;
        int pageSize = sizeParam != null && sizeParam.intValue() > 0 ? sizeParam.intValue() : 10;

        Integer resolvedStatusId = statusId;
        if (resolvedStatusId == null && onlyAvailable) {
            try {
                resolvedStatusId = ReservationServletHelper.resolveVehicleStatusId(request, vehicleStatusService,
                        "DISPONIBLE", "AVAILABLE");
            } catch (RentexpresException ex) {
                LOGGER.error("Error resolving available vehicle status id", ex);
            }
        }

        VehicleCriteria criteria = buildCriteria(brand, model, categoryId, resolvedStatusId, minPrice, maxPrice, pageNumber,
                pageSize);

        Map<String, Object> activeFilters = new LinkedHashMap<>();
        if (search != null) {
            activeFilters.put(VehicleConstants.PARAM_SEARCH, search);
        }
        if (brand != null) {
            activeFilters.put(VehicleConstants.PARAM_BRAND, brand);
        }
        if (model != null) {
            activeFilters.put(VehicleConstants.PARAM_MODEL, model);
        }
        if (categoryId != null) {
            activeFilters.put(VehicleConstants.PARAM_CATEGORY, categoryId);
        }
        if (statusId != null) {
            activeFilters.put(VehicleConstants.PARAM_STATUS, statusId);
            activeFilters.put(VehicleConstants.PARAM_STATUS_ID, statusId);
        }
        if (minPrice != null) {
            activeFilters.put(VehicleConstants.PARAM_MIN_PRICE, minPrice);
            activeFilters.put(VehicleConstants.PARAM_MIN_PRICE_ALT, minPrice);
        }
        if (maxPrice != null) {
            activeFilters.put(VehicleConstants.PARAM_MAX_PRICE, maxPrice);
            activeFilters.put(VehicleConstants.PARAM_MAX_PRICE_ALT, maxPrice);
        }
        if (onlyAvailable) {
            activeFilters.put(VehicleConstants.PARAM_ONLY_AVAILABLE, Boolean.TRUE);
        }
        Integer pageAttribute = Integer.valueOf(pageNumber);
        Integer sizeAttribute = Integer.valueOf(pageSize);
        activeFilters.put(VehicleConstants.PARAM_PAGE, pageAttribute);
        activeFilters.put(VehicleConstants.PARAM_PAGE_SIZE, sizeAttribute);
        activeFilters.put(VehicleConstants.PARAM_PAGE_SIZE_ALIAS, sizeAttribute);
        request.setAttribute(VehicleConstants.ATTR_FILTERS, activeFilters);

        Boolean onlyAvailableFlag = Boolean.valueOf(onlyAvailable);
        request.setAttribute("search", search);
        request.setAttribute(VehicleConstants.PARAM_SEARCH, search);
        request.setAttribute("brand", brand);
        request.setAttribute(VehicleConstants.PARAM_BRAND, brand);
        request.setAttribute("model", model);
        request.setAttribute(VehicleConstants.PARAM_MODEL, model);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute(VehicleConstants.PARAM_CATEGORY, categoryId);
        request.setAttribute("status", statusId);
        request.setAttribute(VehicleConstants.PARAM_STATUS, statusId);
        request.setAttribute(VehicleConstants.PARAM_STATUS_ID, statusId);
        request.setAttribute("priceMin", minPrice);
        request.setAttribute("minPrice", minPrice);
        request.setAttribute(VehicleConstants.PARAM_MIN_PRICE, minPrice);
        request.setAttribute(VehicleConstants.PARAM_MIN_PRICE_ALT, minPrice);
        request.setAttribute("priceMax", maxPrice);
        request.setAttribute("maxPrice", maxPrice);
        request.setAttribute(VehicleConstants.PARAM_MAX_PRICE, maxPrice);
        request.setAttribute(VehicleConstants.PARAM_MAX_PRICE_ALT, maxPrice);
        request.setAttribute("availableOnly", onlyAvailableFlag);
        request.setAttribute(VehicleConstants.PARAM_ONLY_AVAILABLE, onlyAvailableFlag);
        request.setAttribute("page", pageAttribute);
        request.setAttribute(VehicleConstants.PARAM_PAGE, pageAttribute);
        request.setAttribute("size", sizeAttribute);
        request.setAttribute("pageSize", sizeAttribute);
        request.setAttribute(VehicleConstants.PARAM_PAGE_SIZE, sizeAttribute);
        request.setAttribute(VehicleConstants.PARAM_PAGE_SIZE_ALIAS, sizeAttribute);

        try {
            Results<VehicleDTO> results = vehicleService.findByCriteria(criteria, pageAttribute, sizeAttribute);
            List<VehicleDTO> vehicles = results != null && results.getResults() != null ? results.getResults()
                    : Collections.emptyList();

            request.setAttribute(VehicleConstants.ATTR_VEHICLES, vehicles);
            request.setAttribute(VehicleConstants.ATTR_RESULTS, results);
            request.setAttribute(VehicleConstants.ATTR_TOTAL_RESULTS,
                    results != null ? results.getTotalRecords() : Integer.valueOf(vehicles.size()));
            if (results != null) {
                request.setAttribute(VehicleConstants.ATTR_RESULTS_FROM_ROW, Integer.valueOf(results.getFromRow()));
                request.setAttribute(VehicleConstants.ATTR_RESULTS_TO_ROW, Integer.valueOf(results.getToRow()));
            } else {
                request.setAttribute(VehicleConstants.ATTR_RESULTS_FROM_ROW, Integer.valueOf(0));
                request.setAttribute(VehicleConstants.ATTR_RESULTS_TO_ROW, Integer.valueOf(0));
            }
        } catch (RentexpresException ex) {
            LOGGER.error("Error retrieving vehicles by criteria", ex);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
            request.setAttribute(VehicleConstants.ATTR_VEHICLES, Collections.emptyList());
            request.setAttribute(VehicleConstants.ATTR_RESULTS, null);
            request.setAttribute(VehicleConstants.ATTR_TOTAL_RESULTS, Integer.valueOf(0));
            request.setAttribute(VehicleConstants.ATTR_RESULTS_FROM_ROW, Integer.valueOf(0));
            request.setAttribute(VehicleConstants.ATTR_RESULTS_TO_ROW, Integer.valueOf(0));
        }

        request.getRequestDispatcher(Views.PUBLIC_VEHICLE_LIST).forward(request, response);
    }

    private VehicleCriteria buildCriteria(String brand, String model, Integer categoryId, Integer statusId,
            BigDecimal minPrice, BigDecimal maxPrice, int pageNumber, int pageSize) {
        VehicleCriteria criteria = new VehicleCriteria();
        criteria.setBrand(brand);
        criteria.setModel(model);
        criteria.setCategoryId(categoryId);
        criteria.setVehicleStatusId(statusId);
        criteria.setDailyPriceMin(minPrice);
        criteria.setDailyPriceMax(maxPrice);
        criteria.setPageNumber(Integer.valueOf(pageNumber));
        criteria.setPageSize(Integer.valueOf(pageSize));
        return criteria;
    }

    private String resolveParameter(HttpServletRequest request, String... names) {
        if (names == null) {
            return null;
        }
        for (String name : names) {
            if (name == null) {
                continue;
            }
            String value = trimToNull(request.getParameter(name));
            if (value != null) {
                return value;
            }
        }
        return null;
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

    private boolean parseBoolean(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return false;
        }
        return "true".equalsIgnoreCase(normalized) || "on".equalsIgnoreCase(normalized)
                || "1".equals(normalized);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

}
