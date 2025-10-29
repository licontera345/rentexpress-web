package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.exception.RentexpresException;
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
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpressweb.security.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Panel interno para la supervisión del catálogo de vehículos.
 */
@WebServlet("/app/vehicles/manage")
public class VehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(VehicleServlet.class);

    private final VehicleService vehicleService = new VehicleServiceImpl();
    private final VehicleCategoryService categoryService = new VehicleCategoryServiceImpl();
    private final VehicleStatusService statusService = new VehicleStatusServiceImpl();

    public VehicleServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        Object currentEmployee = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        if (currentUser == null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "Inicia sesión para consultar el catálogo interno.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }
        if (currentEmployee == null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "No dispones de permisos para gestionar los vehículos.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.HOME_ENDPOINT);
            return;
        }

        disableCaching(response);
        exposeFlashMessages(request);

        Map<String, String> filters = buildFilters(request);
        List<String> errors = new ArrayList<String>();

        Integer categoryId = parseInteger(filters.get(VehicleConstants.PARAM_CATEGORY), errors,
                "Selecciona una categoría válida.");
        Integer statusId = parseInteger(filters.get(VehicleConstants.PARAM_STATUS), errors,
                "Selecciona un estado válido.");
        int page = parsePage(filters.get(VehicleConstants.PARAM_PAGE), errors);
        int pageSize = parsePageSize(filters.get(VehicleConstants.PARAM_PAGE_SIZE), errors);

        VehicleCriteria criteria = buildCriteria(filters.get(VehicleConstants.PARAM_SEARCH), categoryId, statusId, page,
                pageSize, filters.get(VehicleConstants.PARAM_SORT));

        Results<VehicleDTO> results = searchVehicles(criteria, errors);

        Locale locale = request.getLocale();
        List<VehicleCategoryDTO> categories = loadCategories(locale);
        List<VehicleStatusDTO> statuses = loadStatuses(locale);
        Map<Integer, String> categoryNames = buildCategoryNames(categories);
        Map<Integer, String> statusNames = buildStatusNames(statuses);

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Gestión interna de vehículos");
        request.setAttribute(VehicleConstants.ATTR_FILTERS, filters);
        request.setAttribute(VehicleConstants.ATTR_FILTER_ERRORS, errors);
        request.setAttribute(VehicleConstants.ATTR_AVAILABLE_CATEGORIES, categories);
        request.setAttribute(VehicleConstants.ATTR_AVAILABLE_STATUSES, statuses);
        request.setAttribute(VehicleConstants.ATTR_CATEGORY_NAMES, categoryNames);
        request.setAttribute(VehicleConstants.ATTR_STATUS_NAMES, statusNames);
        request.setAttribute(VehicleConstants.ATTR_RESULTS, results);
        request.setAttribute(VehicleConstants.ATTR_VEHICLES, results.getItems());
        request.setAttribute(VehicleConstants.ATTR_TOTAL_RESULTS, Integer.valueOf(results.getTotal()));

        request.getRequestDispatcher("/private/vehicle/vehicle_list.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private Map<String, String> buildFilters(HttpServletRequest request) {
        Map<String, String> filters = new HashMap<String, String>();
        filters.put(VehicleConstants.PARAM_SEARCH, trimToNull(request.getParameter(VehicleConstants.PARAM_SEARCH)));
        filters.put(VehicleConstants.PARAM_CATEGORY, trimToNull(request.getParameter(VehicleConstants.PARAM_CATEGORY)));
        filters.put(VehicleConstants.PARAM_STATUS, trimToNull(request.getParameter(VehicleConstants.PARAM_STATUS)));
        String page = request.getParameter(VehicleConstants.PARAM_PAGE);
        if (page == null || page.trim().isEmpty()) {
            page = "1";
        }
        filters.put(VehicleConstants.PARAM_PAGE, page);
        String pageSize = request.getParameter(VehicleConstants.PARAM_PAGE_SIZE);
        if (pageSize == null || pageSize.trim().isEmpty()) {
            pageSize = "20";
        }
        filters.put(VehicleConstants.PARAM_PAGE_SIZE, pageSize);
        String sort = request.getParameter(VehicleConstants.PARAM_SORT);
        if (sort == null || sort.trim().isEmpty()) {
            sort = VehicleConstants.VALUE_SORT_PRICE_ASC;
        }
        filters.put(VehicleConstants.PARAM_SORT, sort);
        return filters;
    }

    private VehicleCriteria buildCriteria(String search, Integer categoryId, Integer statusId, int page, int pageSize,
            String sort) {
        VehicleCriteria criteria = new VehicleCriteria();
        criteria.setCategoryId(categoryId);
        criteria.setVehicleStatusId(statusId);
        criteria.setPage(Integer.valueOf(page));
        criteria.setPageSize(Integer.valueOf(pageSize));

        if (search != null && !search.isEmpty()) {
            String[] parts = search.split("\\s+", 2);
            criteria.setBrand(parts[0]);
            if (parts.length > 1) {
                criteria.setModel(parts[1]);
            }
        }

        applySorting(criteria, sort);
        return criteria;
    }

    private Results<VehicleDTO> searchVehicles(VehicleCriteria criteria, List<String> errors) {
        try {
            Results<VehicleDTO> results = vehicleService.findByCriteria(criteria);
            if (results == null) {
                return emptyResults(criteria);
            }
            results.normalize();
            return results;
        } catch (RentexpresException ex) {
            LOGGER.error("Error recuperando los vehículos", ex);
            errors.add("No se pudo recuperar el listado de vehículos.");
            return emptyResults(criteria);
        }
    }

    private Results<VehicleDTO> emptyResults(VehicleCriteria criteria) {
        Results<VehicleDTO> results = new Results<VehicleDTO>();
        results.setItems(new ArrayList<VehicleDTO>());
        results.setPage(criteria.getSafePage());
        results.setPageSize(criteria.getSafePageSize());
        results.setTotal(0);
        results.normalize();
        return results;
    }

    private void applySorting(VehicleCriteria criteria, String sort) {
        if (VehicleConstants.VALUE_SORT_PRICE_DESC.equals(sort)) {
            criteria.setOrderBy("daily_price");
            criteria.setOrderDir("DESC");
        } else if (VehicleConstants.VALUE_SORT_YEAR_DESC.equals(sort)) {
            criteria.setOrderBy("manufacture_year");
            criteria.setOrderDir("DESC");
        } else {
            criteria.setOrderBy("daily_price");
            criteria.setOrderDir("ASC");
        }
    }

    private List<VehicleCategoryDTO> loadCategories(Locale locale) {
        try {
            String language = locale != null ? locale.getLanguage() : Locale.getDefault().getLanguage();
            List<VehicleCategoryDTO> list = categoryService.findAll(language);
            if (list == null) {
                return new ArrayList<VehicleCategoryDTO>();
            }
            return list;
        } catch (RentexpresException ex) {
            LOGGER.error("No se pudieron obtener las categorías", ex);
            return new ArrayList<VehicleCategoryDTO>();
        }
    }

    private List<VehicleStatusDTO> loadStatuses(Locale locale) {
        try {
            String language = locale != null ? locale.getLanguage() : Locale.getDefault().getLanguage();
            List<VehicleStatusDTO> list = statusService.findAll(language);
            if (list == null) {
                return new ArrayList<VehicleStatusDTO>();
            }
            return list;
        } catch (RentexpresException ex) {
            LOGGER.error("No se pudieron obtener los estados de vehículo", ex);
            return new ArrayList<VehicleStatusDTO>();
        }
    }

    private Map<Integer, String> buildCategoryNames(List<VehicleCategoryDTO> categories) {
        Map<Integer, String> names = new LinkedHashMap<Integer, String>();
        for (int i = 0; i < categories.size(); i++) {
            VehicleCategoryDTO category = categories.get(i);
            if (category != null && category.getCategoryId() != null) {
                names.put(category.getCategoryId(), category.getCategoryName());
            }
        }
        return names;
    }

    private Map<Integer, String> buildStatusNames(List<VehicleStatusDTO> statuses) {
        Map<Integer, String> names = new LinkedHashMap<Integer, String>();
        for (int i = 0; i < statuses.size(); i++) {
            VehicleStatusDTO status = statuses.get(i);
            if (status != null && status.getVehicleStatusId() != null) {
                names.put(status.getVehicleStatusId(), status.getStatusName());
            }
        }
        return names;
    }

    private Integer parseInteger(String value, List<String> errors, String message) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            errors.add(message);
            return null;
        }
    }

    private int parsePage(String value, List<String> errors) {
        if (value == null || value.trim().isEmpty()) {
            return 1;
        }
        try {
            int page = Integer.parseInt(value.trim());
            return page < 1 ? 1 : page;
        } catch (NumberFormatException ex) {
            errors.add("La página indicada no es válida.");
            return 1;
        }
    }

    private int parsePageSize(String value, List<String> errors) {
        if (value == null || value.trim().isEmpty()) {
            return 20;
        }
        try {
            int size = Integer.parseInt(value.trim());
            if (size < 5) {
                return 5;
            }
            if (size > 100) {
                return 100;
            }
            return size;
        } catch (NumberFormatException ex) {
            errors.add("El tamaño de página indicado no es válido.");
            return 20;
        }
    }

    private void exposeFlashMessages(HttpServletRequest request) {
        Object success = SessionManager.getAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        if (success != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_SUCCESS, success);
            SessionManager.removeAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        }
        Object error = SessionManager.getAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        if (error != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, error);
            SessionManager.removeAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        }
    }

    private void disableCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
