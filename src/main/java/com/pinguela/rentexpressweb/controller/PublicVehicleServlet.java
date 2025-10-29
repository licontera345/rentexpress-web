package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.dao.HeadquartersDAO;
import com.pinguela.rentexpres.dao.impl.HeadquartersDAOImpl;
import com.pinguela.rentexpres.exception.DataException;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.VehicleCategoryDTO;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.model.VehicleStatusDTO;
import com.pinguela.rentexpres.service.VehicleCategoryService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.impl.VehicleCategoryServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpres.util.JDBCUtils;
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
import java.util.Set;
import java.util.HashSet;
import java.sql.Connection;
import java.sql.SQLException;
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
    private final HeadquartersDAO headquartersDAO = new HeadquartersDAOImpl();

    private static final int DEFAULT_AVAILABLE_STATUS_ID = 1;
    private static final Set<String> AVAILABLE_STATUS_KEYWORDS = new HashSet<>();

    static {
        AVAILABLE_STATUS_KEYWORDS.add("disponible");
        AVAILABLE_STATUS_KEYWORDS.add("available");
        AVAILABLE_STATUS_KEYWORDS.add("libre");
    }

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
        Integer headquartersId = parseHeadquarters(filters.get(VehicleConstants.PARAM_HEADQUARTERS), filterErrors);
        boolean onlyAvailable = Boolean.parseBoolean(filters.get(VehicleConstants.PARAM_ONLY_AVAILABLE));

        List<VehicleDTO> allVehicles = loadVehicles();
        List<VehicleCategoryDTO> categories = loadCategories(locale);
        Map<Integer, String> categoryNames = buildCategoryNames(categories);
        List<HeadquartersDTO> headquarters = loadHeadquarters();
        Map<Integer, String> headquartersNames = mapHeadquarters(headquarters);

        List<VehicleDTO> vehicles = filterVehicles(allVehicles,
                filters.get(VehicleConstants.PARAM_SEARCH),
                categoryId,
                minPrice,
                maxPrice,
                filters.get(VehicleConstants.PARAM_SORT),
                headquartersId,
                onlyAvailable);

        if (request.getParameter("notfound") != null) {
            filterErrors.add("El vehículo solicitado no existe o ya no está disponible. Se ha mostrado el catálogo completo.");
        }

        request.setAttribute(VehicleConstants.ATTR_VEHICLES, vehicles);
        request.setAttribute(VehicleConstants.ATTR_TOTAL_RESULTS, vehicles.size());
        request.setAttribute(VehicleConstants.ATTR_AVAILABLE_CATEGORIES, categories);
        request.setAttribute(VehicleConstants.ATTR_CATEGORY_NAMES, categoryNames);
        request.setAttribute(VehicleConstants.ATTR_HEADQUARTERS, headquarters);
        request.setAttribute(VehicleConstants.ATTR_HEADQUARTERS_NAMES, headquartersNames);
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
        filters.put(VehicleConstants.PARAM_HEADQUARTERS, sanitize(request.getParameter(VehicleConstants.PARAM_HEADQUARTERS)));

        String sort = sanitize(request.getParameter(VehicleConstants.PARAM_SORT));
        if (!VehicleConstants.VALUE_SORT_PRICE_ASC.equals(sort)
                && !VehicleConstants.VALUE_SORT_PRICE_DESC.equals(sort)
                && !VehicleConstants.VALUE_SORT_YEAR_DESC.equals(sort)) {
            sort = VehicleConstants.VALUE_SORT_PRICE_ASC;
        }
        filters.put(VehicleConstants.PARAM_SORT, sort);
        boolean onlyAvailable = parseBooleanFlag(request.getParameter(VehicleConstants.PARAM_ONLY_AVAILABLE));
        filters.put(VehicleConstants.PARAM_ONLY_AVAILABLE, Boolean.toString(onlyAvailable));
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

    private Integer parseHeadquarters(String rawValue, List<String> errors) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(rawValue.trim());
        } catch (NumberFormatException ex) {
            errors.add("La sede seleccionada no es válida.");
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
            BigDecimal minPrice, BigDecimal maxPrice, String sort, Integer headquartersId, boolean onlyAvailable) {
        if (vehicles == null || vehicles.isEmpty()) {
            return new ArrayList<>();
        }

        return vehicles.stream()
                .filter(vehicle -> matchesCategory(vehicle, categoryId))
                .filter(vehicle -> matchesSearch(vehicle, search))
                .filter(vehicle -> matchesMinPrice(vehicle, minPrice))
                .filter(vehicle -> matchesMaxPrice(vehicle, maxPrice))
                .filter(vehicle -> matchesHeadquarters(vehicle, headquartersId))
                .filter(vehicle -> matchesAvailability(vehicle, onlyAvailable))
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

    private boolean matchesHeadquarters(VehicleDTO vehicle, Integer headquartersId) {
        if (headquartersId == null) {
            return true;
        }
        if (vehicle.getCurrentHeadquartersId() != null) {
            return headquartersId.equals(vehicle.getCurrentHeadquartersId());
        }
        return vehicle.getCurrentHeadquarters() != null
                && headquartersId.equals(vehicle.getCurrentHeadquarters().getHeadquartersId());
    }

    private boolean matchesAvailability(VehicleDTO vehicle, boolean onlyAvailable) {
        if (!onlyAvailable) {
            return true;
        }
        Integer statusId = vehicle.getVehicleStatusId();
        if (statusId != null && statusId.intValue() == DEFAULT_AVAILABLE_STATUS_ID) {
            return true;
        }
        VehicleStatusDTO status = vehicle.getVehicleStatus();
        if (status == null || status.getStatusName() == null) {
            return false;
        }
        String normalized = status.getStatusName().trim().toLowerCase(Locale.ROOT);
        for (String keyword : AVAILABLE_STATUS_KEYWORDS) {
            if (normalized.contains(keyword)) {
                return true;
            }
        }
        return false;
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

    private List<HeadquartersDTO> loadHeadquarters() {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            JDBCUtils.beginTransaction(connection);
            List<HeadquartersDTO> list = headquartersDAO.findAll(connection);
            JDBCUtils.commitTransaction(connection);
            return list;
        } catch (SQLException | DataException ex) {
            JDBCUtils.rollbackTransaction(connection);
            LOGGER.error("Error al recuperar las sedes", ex);
            return new ArrayList<>();
        } finally {
            JDBCUtils.close(connection);
        }
    }

    private Map<Integer, String> mapHeadquarters(List<HeadquartersDTO> headquarters) {
        Map<Integer, String> names = new LinkedHashMap<>();
        if (headquarters != null) {
            for (HeadquartersDTO dto : headquarters) {
                if (dto == null || dto.getHeadquartersId() == null) {
                    continue;
                }
                StringBuilder label = new StringBuilder();
                if (dto.getName() != null) {
                    label.append(dto.getName().trim());
                }
                String city = dto.getCity() != null ? dto.getCity().getCityName() : null;
                String province = dto.getProvince() != null ? dto.getProvince().getProvinceName() : null;
                List<String> locationParts = new ArrayList<>();
                if (city != null && !city.trim().isEmpty()) {
                    locationParts.add(city.trim());
                }
                if (province != null && !province.trim().isEmpty()) {
                    locationParts.add(province.trim());
                }
                if (!locationParts.isEmpty()) {
                    if (label.length() > 0) {
                        label.append(" · ");
                    }
                    label.append(String.join(", ", locationParts));
                }
                names.put(dto.getHeadquartersId(), label.toString());
            }
        }
        return names;
    }

    private boolean parseBooleanFlag(String rawValue) {
        if (rawValue == null) {
            return false;
        }
        String normalized = rawValue.trim().toLowerCase(Locale.ROOT);
        return "true".equals(normalized) || "on".equals(normalized) || "1".equals(normalized)
                || "yes".equals(normalized) || "si".equals(normalized) || "sí".equals(normalized);
    }

    private String sanitize(String value) {
        return value != null ? value.trim() : null;
    }
}
