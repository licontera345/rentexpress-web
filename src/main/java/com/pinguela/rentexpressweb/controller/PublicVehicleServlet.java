package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.model.VehicleCategoryDTO;
import com.pinguela.rentexpres.model.VehicleCriteria;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.model.VehicleStatusDTO;
import com.pinguela.rentexpres.service.VehicleCategoryService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.VehicleStatusService;
import com.pinguela.rentexpres.service.impl.VehicleCategoryServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleStatusServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/vehicles")
public class PublicVehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(PublicVehicleServlet.class);
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int DEFAULT_AVAILABLE_STATUS_ID = 1;
    private final VehicleService vehicleService = new VehicleServiceImpl();
    private final VehicleCategoryService categoryService = new VehicleCategoryServiceImpl();
    private final VehicleStatusService statusService = new VehicleStatusServiceImpl();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> errors = new ArrayList<String>();
        String search = trim(request.getParameter("search"));
        String minPriceValue = trim(request.getParameter("minPrice"));
        String maxPriceValue = trim(request.getParameter("maxPrice"));
        Integer categoryId = parseInteger(trim(request.getParameter("category")), "La categoría seleccionada no es válida.", errors);
        Integer statusId = parseInteger(trim(request.getParameter("status")), "El estado seleccionado no es válido.", errors);
        BigDecimal minPrice = parsePrice(minPriceValue, "El precio mínimo debe ser un número positivo.", errors);
        BigDecimal maxPrice = parsePrice(maxPriceValue, "El precio máximo debe ser un número positivo.", errors);
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) { errors.add("El precio mínimo no puede ser mayor que el máximo."); }
        boolean onlyAvailable = parseBoolean(request.getParameter("onlyAvailable"));
        int page = parsePage(request.getParameter("page"));
        int size = parseSize(request.getParameter("size"), errors);

        Locale locale = request.getLocale();
        String language = locale != null ? locale.getLanguage() : Locale.getDefault().getLanguage();
        List<VehicleCategoryDTO> categories = loadCategories(language);
        List<VehicleStatusDTO> statuses = loadStatuses(language);
        Integer availableStatusId = findAvailableStatusId(statuses);

        VehicleCriteria criteria = new VehicleCriteria();
        if (search != null) { criteria.setBrand(search); }
        criteria.setCategoryId(categoryId);
        criteria.setVehicleStatusId(resolveStatusId(statusId, onlyAvailable, availableStatusId));
        criteria.setDailyPriceMin(minPrice);
        criteria.setDailyPriceMax(maxPrice);
        criteria.setPageNumber(Integer.valueOf(page));
        criteria.setPageSize(Integer.valueOf(size));

        List<VehicleDTO> items = new ArrayList<VehicleDTO>();
        int total = 0;
        try {
            Results<VehicleDTO> results = vehicleService.findByCriteria(criteria);
            if (results != null) {
                if (results.getResults() != null) { items = results.getResults(); }
                if (results.getTotalRecords() != null) { total = results.getTotalRecords().intValue(); } else { total = items.size(); }
                if (results.getPageNumber() != null) { page = results.getPageNumber().intValue(); }
                if (results.getPageSize() != null) { size = results.getPageSize().intValue(); }
            }
        } catch (Exception ex) {
            LOGGER.error("Error al recuperar el catálogo público de vehículos", ex);
            errors.add("No se pudo cargar el catálogo en este momento. Inténtalo de nuevo más tarde.");
        }

        int totalPages = size > 0 ? (int) Math.ceil((double) total / (double) size) : 1;
        if (totalPages == 0) { totalPages = 1; }
        if (page > totalPages) { page = totalPages; }
        int from = 0;
        int to = 0;
        if (!items.isEmpty() && size > 0) {
            from = ((page - 1) * size) + 1;
            if (from > total) { from = total; }
            to = from + items.size() - 1;
            if (to > total) { to = total; }
        }

        request.setAttribute("pageTitle", "Catálogo de vehículos");
        if (!errors.isEmpty()) { request.setAttribute("error", errors); }
        request.setAttribute("items", items);
        request.setAttribute("categories", categories);
        request.setAttribute("statuses", statuses);
        request.setAttribute("search", search);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("statusId", statusId);
        request.setAttribute("minPrice", minPriceValue);
        request.setAttribute("maxPrice", maxPriceValue);
        request.setAttribute("onlyAvailable", Boolean.valueOf(onlyAvailable));
        request.setAttribute("page", Integer.valueOf(page));
        request.setAttribute("size", Integer.valueOf(size));
        request.setAttribute("total", Integer.valueOf(total));
        request.setAttribute("from", Integer.valueOf(from));
        request.setAttribute("to", Integer.valueOf(to));
        request.setAttribute("totalPages", Integer.valueOf(totalPages));
        request.setAttribute("pageSizes", Arrays.asList(Integer.valueOf(10), Integer.valueOf(20), Integer.valueOf(50)));

        request.getRequestDispatcher("/public/vehicle/catalog.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { doGet(request, response); }

    private String trim(String value) {
        if (value == null) { return null; }
        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    private Integer parseInteger(String value, String message, List<String> errors) {
        if (value == null) { return null; }
        try { return Integer.valueOf(value); } catch (NumberFormatException ex) { errors.add(message); return null; }
    }

    private BigDecimal parsePrice(String value, String message, List<String> errors) {
        if (value == null) { return null; }
        try {
            BigDecimal price = new BigDecimal(value);
            if (price.compareTo(BigDecimal.ZERO) < 0) { errors.add(message); return null; }
            return price;
        } catch (NumberFormatException ex) { errors.add(message); return null; }
    }

    private boolean parseBoolean(String value) { return value != null && "true".equalsIgnoreCase(value); }

    private int parsePage(String value) {
        try { return value == null ? DEFAULT_PAGE : Math.max(Integer.parseInt(value), 1); }
        catch (NumberFormatException ex) { return DEFAULT_PAGE; }
    }

    private int parseSize(String value, List<String> errors) {
        try {
            if (value != null) {
                int parsed = Integer.parseInt(value);
                if (parsed == 10 || parsed == 20 || parsed == 50) { return parsed; }
            }
        } catch (NumberFormatException ex) {
            // ignored
        }
        errors.add("El tamaño de página seleccionado no es válido.");
        return DEFAULT_SIZE;
    }

    private List<VehicleCategoryDTO> loadCategories(String language) {
        try {
            List<VehicleCategoryDTO> categories = categoryService.findAll(language);
            return categories != null ? categories : new ArrayList<VehicleCategoryDTO>();
        } catch (Exception ex) {
            LOGGER.error("Error al recuperar las categorías de vehículos", ex);
            return new ArrayList<VehicleCategoryDTO>();
        }
    }

    private List<VehicleStatusDTO> loadStatuses(String language) {
        try {
            List<VehicleStatusDTO> statuses = statusService.findAll(language);
            return statuses != null ? statuses : new ArrayList<VehicleStatusDTO>();
        } catch (Exception ex) {
            LOGGER.error("Error al recuperar los estados de los vehículos", ex);
            return new ArrayList<VehicleStatusDTO>();
        }
    }

    private Integer findAvailableStatusId(List<VehicleStatusDTO> statuses) {
        if (statuses == null) { return Integer.valueOf(DEFAULT_AVAILABLE_STATUS_ID); }
        for (int i = 0; i < statuses.size(); i++) {
            VehicleStatusDTO status = statuses.get(i);
            if (status == null || status.getStatusName() == null || status.getVehicleStatusId() == null) { continue; }
            String normalized = status.getStatusName().trim().toLowerCase(Locale.ROOT);
            if (normalized.contains("disponible") || normalized.contains("available") || normalized.contains("libre")) {
                return status.getVehicleStatusId();
            }
        }
        return Integer.valueOf(DEFAULT_AVAILABLE_STATUS_ID);
    }

    private Integer resolveStatusId(Integer statusId, boolean onlyAvailable, Integer availableStatusId) {
        if (statusId != null) { return statusId; }
        if (onlyAvailable) { return availableStatusId; }
        return null;
    }
}
