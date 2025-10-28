package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.VehicleCategoryDTO;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.VehicleCategoryService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.impl.VehicleCategoryServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet encargado de mostrar el catálogo público de vehículos con filtros básicos.
 */
@WebServlet("/public/vehicles")
public class PublicVehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PublicVehicleServlet.class);

    private final VehicleService vehicleService = new VehicleServiceImpl();
    private final VehicleCategoryService categoryService = new VehicleCategoryServiceImpl();

    public PublicVehicleServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Catálogo de vehículos");

        Map<String, String> filters = buildFilters(request);
        List<String> filterErrors = new ArrayList<>();
        Locale locale = request.getLocale();

        BigDecimal minPrice = parsePrice(filters.get(VehicleConstants.PARAM_MIN_PRICE), filterErrors,
                "El precio mínimo debe tener un formato numérico válido.");
        BigDecimal maxPrice = parsePrice(filters.get(VehicleConstants.PARAM_MAX_PRICE), filterErrors,
                "El precio máximo debe tener un formato numérico válido.");

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            filterErrors.add("El precio mínimo no puede ser mayor que el máximo.");
        }

        Integer categoryId = parseCategory(filters.get(VehicleConstants.PARAM_CATEGORY), filterErrors);

        List<VehicleDTO> allVehicles = loadVehicles();
        List<VehicleCategoryDTO> categories = loadCategories(locale);
        Map<Integer, String> categoryNames = buildCategoryNames(categories);

        List<VehicleDTO> vehicles = filterVehicles(allVehicles,
                filters.get(VehicleConstants.PARAM_SEARCH),
                categoryId,
                minPrice,
                maxPrice,
                filters.get(VehicleConstants.PARAM_SORT));

        if (request.getParameter("notfound") != null) {
            filterErrors.add("El vehículo solicitado no existe o ya no está disponible. Se ha mostrado el catálogo completo.");
        }

        request.setAttribute(VehicleConstants.ATTR_VEHICLES, vehicles);
        request.setAttribute(VehicleConstants.ATTR_TOTAL_RESULTS, vehicles.size());
        request.setAttribute(VehicleConstants.ATTR_AVAILABLE_CATEGORIES, categories);
        request.setAttribute(VehicleConstants.ATTR_CATEGORY_NAMES, categoryNames);
        request.setAttribute(VehicleConstants.ATTR_FILTERS, filters);
        request.setAttribute(VehicleConstants.ATTR_FILTER_ERRORS, filterErrors);
        request.getRequestDispatcher("/public/vehicle/catalog.jsp").forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private Map<String, String> buildFilters(HttpServletRequest request) {
        Map<String, String> filters = new HashMap<>();
        filters.put(VehicleConstants.PARAM_SEARCH, sanitize(request.getParameter(VehicleConstants.PARAM_SEARCH)));
        filters.put(VehicleConstants.PARAM_CATEGORY, sanitize(request.getParameter(VehicleConstants.PARAM_CATEGORY)));
        filters.put(VehicleConstants.PARAM_MIN_PRICE, sanitize(request.getParameter(VehicleConstants.PARAM_MIN_PRICE)));
        filters.put(VehicleConstants.PARAM_MAX_PRICE, sanitize(request.getParameter(VehicleConstants.PARAM_MAX_PRICE)));

        String sort = sanitize(request.getParameter(VehicleConstants.PARAM_SORT));
        if (!VehicleConstants.VALUE_SORT_PRICE_ASC.equals(sort)
                && !VehicleConstants.VALUE_SORT_PRICE_DESC.equals(sort)
                && !VehicleConstants.VALUE_SORT_YEAR_DESC.equals(sort)) {
            sort = VehicleConstants.VALUE_SORT_PRICE_ASC;
        }
        filters.put(VehicleConstants.PARAM_SORT, sort);
        return filters;
    }

    private BigDecimal parsePrice(String rawValue, List<String> errors, String errorMessage) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }
        try {
            BigDecimal price = new BigDecimal(rawValue.trim());
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Los importes deben ser positivos.");
                return null;
            }
            return price;
        } catch (NumberFormatException ex) {
            errors.add(errorMessage);
            return null;
        }
    }

    private Integer parseCategory(String rawValue, List<String> errors) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(rawValue.trim());
        } catch (NumberFormatException ex) {
            errors.add("La categoría seleccionada no es válida.");
            return null;
        }
    }

    private List<VehicleDTO> loadVehicles() {
        try {
            return vehicleService.findAll();
        } catch (RentexpresException ex) {
            LOGGER.error("Error al cargar el catálogo de vehículos", ex);
            return new ArrayList<>();
        }
    }

    private List<VehicleCategoryDTO> loadCategories(Locale locale) {
        try {
            String language = locale != null ? locale.getLanguage() : Locale.getDefault().getLanguage();
            return categoryService.findAll(language);
        } catch (RentexpresException ex) {
            LOGGER.error("Error al recuperar las categorías de vehículos", ex);
            return new ArrayList<>();
        }
    }

    private Map<Integer, String> buildCategoryNames(List<VehicleCategoryDTO> categories) {
        Map<Integer, String> names = new LinkedHashMap<>();
        if (categories != null) {
            for (VehicleCategoryDTO category : categories) {
                if (category != null && category.getCategoryId() != null) {
                    names.put(category.getCategoryId(), category.getCategoryName());
                }
            }
        }
        return names;
    }

    private List<VehicleDTO> filterVehicles(List<VehicleDTO> vehicles, String search, Integer categoryId,
            BigDecimal minPrice, BigDecimal maxPrice, String sort) {
        if (vehicles == null || vehicles.isEmpty()) {
            return new ArrayList<>();
        }

        return vehicles.stream()
                .filter(vehicle -> matchesCategory(vehicle, categoryId))
                .filter(vehicle -> matchesSearch(vehicle, search))
                .filter(vehicle -> matchesMinPrice(vehicle, minPrice))
                .filter(vehicle -> matchesMaxPrice(vehicle, maxPrice))
                .sorted(resolveComparator(sort))
                .collect(Collectors.toList());
    }

    private boolean matchesCategory(VehicleDTO vehicle, Integer categoryId) {
        if (categoryId == null) {
            return true;
        }
        return categoryId.equals(vehicle.getCategoryId());
    }

    private boolean matchesSearch(VehicleDTO vehicle, String search) {
        if (search == null || search.trim().isEmpty()) {
            return true;
        }
        String combined = (vehicle.getBrand() + " " + vehicle.getModel()).toLowerCase(Locale.ROOT);
        return combined.contains(search.toLowerCase(Locale.ROOT));
    }

    private boolean matchesMinPrice(VehicleDTO vehicle, BigDecimal minPrice) {
        if (minPrice == null) {
            return true;
        }
        BigDecimal price = vehicle.getDailyPrice();
        return price == null || price.compareTo(minPrice) >= 0;
    }

    private boolean matchesMaxPrice(VehicleDTO vehicle, BigDecimal maxPrice) {
        if (maxPrice == null) {
            return true;
        }
        BigDecimal price = vehicle.getDailyPrice();
        return price == null || price.compareTo(maxPrice) <= 0;
    }

    private Comparator<VehicleDTO> resolveComparator(String sort) {
        if (VehicleConstants.VALUE_SORT_PRICE_DESC.equals(sort)) {
            return Comparator.comparing(VehicleDTO::getDailyPrice, Comparator.nullsLast(BigDecimal::compareTo)).reversed();
        }
        if (VehicleConstants.VALUE_SORT_YEAR_DESC.equals(sort)) {
            return Comparator.comparing(VehicleDTO::getManufactureYear, Comparator.nullsLast(Integer::compare)).reversed();
        }
        return Comparator.comparing(VehicleDTO::getDailyPrice, Comparator.nullsLast(BigDecimal::compareTo));
    }

    private String sanitize(String value) {
        return value != null ? value.trim() : null;
    }
}
