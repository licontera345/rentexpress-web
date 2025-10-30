package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.exception.DataException;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.VehicleCategoryDTO;
import com.pinguela.rentexpres.model.Results;
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
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpres.service.HeadquartersService;
import com.pinguela.rentexpres.service.impl.HeadquartersServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
    private final VehicleStatusService statusService = new VehicleStatusServiceImpl();
    private final HeadquartersService headquartersService = new HeadquartersServiceImpl();

    private static final int DEFAULT_AVAILABLE_STATUS_ID = 1;
    private static final Set<String> AVAILABLE_STATUS_KEYWORDS = new HashSet<>();
    private static final List<Integer> PAGE_SIZE_OPTIONS = Collections
            .unmodifiableList(Arrays.asList(10, 20, 25, 50, 100));
    private static final Map<String, String> PARAM_NAMES;
    private static final Map<String, String> SORT_VALUES;

    static {
        AVAILABLE_STATUS_KEYWORDS.add("disponible");
        AVAILABLE_STATUS_KEYWORDS.add("available");
        AVAILABLE_STATUS_KEYWORDS.add("libre");

        Map<String, String> paramNames = new LinkedHashMap<String, String>();
        paramNames.put("search", VehicleConstants.PARAM_SEARCH);
        paramNames.put("brand", VehicleConstants.PARAM_BRAND);
        paramNames.put("model", VehicleConstants.PARAM_MODEL);
        paramNames.put("category", VehicleConstants.PARAM_CATEGORY);
        paramNames.put("minPrice", VehicleConstants.PARAM_MIN_PRICE);
        paramNames.put("maxPrice", VehicleConstants.PARAM_MAX_PRICE);
        paramNames.put("status", VehicleConstants.PARAM_STATUS);
        paramNames.put("minYear", VehicleConstants.PARAM_MIN_YEAR);
        paramNames.put("maxYear", VehicleConstants.PARAM_MAX_YEAR);
        paramNames.put("sort", VehicleConstants.PARAM_SORT);
        paramNames.put("onlyAvailable", VehicleConstants.PARAM_ONLY_AVAILABLE);
        paramNames.put("page", VehicleConstants.PARAM_PAGE);
        paramNames.put("pageSize", VehicleConstants.PARAM_PAGE_SIZE);
        paramNames.put("headquarters", VehicleConstants.PARAM_HEADQUARTERS);
        paramNames.put("pickupDate", VehicleConstants.PARAM_PICKUP_DATE);
        paramNames.put("pickupTime", VehicleConstants.PARAM_PICKUP_TIME);
        paramNames.put("returnDate", VehicleConstants.PARAM_RETURN_DATE);
        paramNames.put("returnTime", VehicleConstants.PARAM_RETURN_TIME);
        paramNames.put("vehicleId", VehicleConstants.PARAM_VEHICLE_ID);
        PARAM_NAMES = Collections.unmodifiableMap(paramNames);

        Map<String, String> sortValues = new LinkedHashMap<String, String>();
        sortValues.put("priceAsc", VehicleConstants.VALUE_SORT_PRICE_ASC);
        sortValues.put("priceDesc", VehicleConstants.VALUE_SORT_PRICE_DESC);
        sortValues.put("yearDesc", VehicleConstants.VALUE_SORT_YEAR_DESC);
        SORT_VALUES = Collections.unmodifiableMap(sortValues);
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

        request.setAttribute(VehicleConstants.ATTR_PARAM_NAMES, PARAM_NAMES);
        request.setAttribute(VehicleConstants.ATTR_SORT_VALUES, SORT_VALUES);

        BigDecimal minPrice = parsePrice(filters.get(VehicleConstants.PARAM_MIN_PRICE), filterErrors,
                "El precio mínimo debe tener un formato numérico válido.");
        BigDecimal maxPrice = parsePrice(filters.get(VehicleConstants.PARAM_MAX_PRICE), filterErrors,
                "El precio máximo debe tener un formato numérico válido.");

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            filterErrors.add("El precio mínimo no puede ser mayor que el máximo.");
        }

        Integer categoryId = parseInteger(filters.get(VehicleConstants.PARAM_CATEGORY), filterErrors,
                "La categoría seleccionada no es válida.");
        Integer headquartersId = parseInteger(filters.get(VehicleConstants.PARAM_HEADQUARTERS), filterErrors,
                "La sede seleccionada no es válida.");
        Integer statusId = parseInteger(filters.get(VehicleConstants.PARAM_STATUS), filterErrors,
                "El estado seleccionado no es válido.");
        Integer minYear = parseYear(filters.get(VehicleConstants.PARAM_MIN_YEAR), filterErrors,
                "El año mínimo debe ser un número de cuatro dígitos.");
        Integer maxYear = parseYear(filters.get(VehicleConstants.PARAM_MAX_YEAR), filterErrors,
                "El año máximo debe ser un número de cuatro dígitos.");

        if (minYear != null && maxYear != null && minYear.intValue() > maxYear.intValue()) {
            filterErrors.add("El año inicial no puede ser mayor que el final.");
        }

        boolean onlyAvailable = Boolean.parseBoolean(filters.get(VehicleConstants.PARAM_ONLY_AVAILABLE));
        int page = parsePage(filters.get(VehicleConstants.PARAM_PAGE), filterErrors);
        int pageSize = parsePageSize(filters.get(VehicleConstants.PARAM_PAGE_SIZE), filterErrors);

        List<VehicleCategoryDTO> categories = loadCategories(locale);
        Map<Integer, String> categoryNames = buildCategoryNames(categories);
        List<HeadquartersDTO> headquarters = loadHeadquarters();
        Map<Integer, String> headquartersNames = mapHeadquarters(headquarters);
        List<VehicleStatusDTO> statuses = loadStatuses(locale);
        Map<Integer, String> statusNames = buildStatusNames(statuses);
        Integer availableStatusId = resolveAvailableStatusId(statuses);

        VehicleCriteria criteria = buildCriteria(filters,
                minPrice,
                maxPrice,
                categoryId,
                headquartersId,
                statusId,
                minYear,
                maxYear,
                onlyAvailable,
                availableStatusId,
                page,
                pageSize);

        Results<VehicleDTO> results = searchVehicles(criteria, filterErrors);
        List<VehicleDTO> vehicles = results.getResults();

        request.setAttribute(VehicleConstants.ATTR_FILTERS, filters);
        request.setAttribute(VehicleConstants.ATTR_FILTER_ERRORS, filterErrors);
        request.setAttribute(VehicleConstants.ATTR_AVAILABLE_CATEGORIES, categories);
        request.setAttribute(VehicleConstants.ATTR_CATEGORY_NAMES, categoryNames);
        request.setAttribute(VehicleConstants.ATTR_HEADQUARTERS, headquarters);
        request.setAttribute(VehicleConstants.ATTR_HEADQUARTERS_NAMES, headquartersNames);
        request.setAttribute(VehicleConstants.ATTR_AVAILABLE_STATUSES, statuses);
        request.setAttribute(VehicleConstants.ATTR_STATUS_NAMES, statusNames);
        request.setAttribute(VehicleConstants.ATTR_PAGE_SIZES, PAGE_SIZE_OPTIONS);
        request.setAttribute(VehicleConstants.ATTR_RESULTS, results);
        request.setAttribute(VehicleConstants.ATTR_VEHICLES, vehicles);
        request.setAttribute(VehicleConstants.ATTR_TOTAL_RESULTS, resolveTotalRecords(results));
        request.setAttribute(VehicleConstants.ATTR_RESULTS_FROM_ROW, resolveFromRow(results));
        request.setAttribute(VehicleConstants.ATTR_RESULTS_TO_ROW, resolveToRow(results));

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
        filters.put(VehicleConstants.PARAM_BRAND, sanitize(request.getParameter(VehicleConstants.PARAM_BRAND)));
        filters.put(VehicleConstants.PARAM_MODEL, sanitize(request.getParameter(VehicleConstants.PARAM_MODEL)));
        filters.put(VehicleConstants.PARAM_CATEGORY, sanitize(request.getParameter(VehicleConstants.PARAM_CATEGORY)));
        filters.put(VehicleConstants.PARAM_MIN_PRICE, sanitize(request.getParameter(VehicleConstants.PARAM_MIN_PRICE)));
        filters.put(VehicleConstants.PARAM_MAX_PRICE, sanitize(request.getParameter(VehicleConstants.PARAM_MAX_PRICE)));
        filters.put(VehicleConstants.PARAM_HEADQUARTERS, sanitize(request.getParameter(VehicleConstants.PARAM_HEADQUARTERS)));
        filters.put(VehicleConstants.PARAM_PICKUP_DATE, sanitize(request.getParameter(VehicleConstants.PARAM_PICKUP_DATE)));
        filters.put(VehicleConstants.PARAM_PICKUP_TIME, sanitize(request.getParameter(VehicleConstants.PARAM_PICKUP_TIME)));
        filters.put(VehicleConstants.PARAM_RETURN_DATE, sanitize(request.getParameter(VehicleConstants.PARAM_RETURN_DATE)));
        filters.put(VehicleConstants.PARAM_RETURN_TIME, sanitize(request.getParameter(VehicleConstants.PARAM_RETURN_TIME)));
        filters.put(VehicleConstants.PARAM_STATUS, sanitize(request.getParameter(VehicleConstants.PARAM_STATUS)));
        filters.put(VehicleConstants.PARAM_MIN_YEAR, sanitize(request.getParameter(VehicleConstants.PARAM_MIN_YEAR)));
        filters.put(VehicleConstants.PARAM_MAX_YEAR, sanitize(request.getParameter(VehicleConstants.PARAM_MAX_YEAR)));
        filters.put(VehicleConstants.PARAM_PAGE, sanitize(request.getParameter(VehicleConstants.PARAM_PAGE)));
        filters.put(VehicleConstants.PARAM_PAGE_SIZE, sanitize(request.getParameter(VehicleConstants.PARAM_PAGE_SIZE)));

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

    private List<VehicleCategoryDTO> loadCategories(Locale locale) {
        try {
            String language = locale != null ? locale.getLanguage() : Locale.getDefault().getLanguage();
            return categoryService.findAll(language);
        } catch (DataException ex) {
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

    private List<HeadquartersDTO> loadHeadquarters() {
        try {
            List<HeadquartersDTO> headquarters = headquartersService.findAll();
            if (headquarters == null) {
                LOGGER.error("Error al recuperar las sedes");
                return new ArrayList<HeadquartersDTO>();
            }
            return headquarters;
        } catch (DataException ex) {
            LOGGER.error("Error al recuperar las sedes", ex);
            return new ArrayList<HeadquartersDTO>();
        }
    }

    private Map<Integer, String> mapHeadquarters(List<HeadquartersDTO> headquarters) {
        Map<Integer, String> names = new LinkedHashMap<>();
        if (headquarters != null) {
            for (HeadquartersDTO dto : headquarters) {
                if (dto == null || dto.getId() == null) {
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
                    for (int i = 0; i < locationParts.size(); i++) {
                        if (i > 0) {
                            label.append(", ");
                        }
                        label.append(locationParts.get(i));
                    }
                }
                names.put(dto.getId(), label.toString());
            }
        }
        return names;
    }

    private List<VehicleStatusDTO> loadStatuses(Locale locale) {
        try {
            String language = locale != null ? locale.getLanguage() : Locale.getDefault().getLanguage();
            return statusService.findAll(language);
        } catch (DataException ex) {
            LOGGER.error("Error al recuperar los estados de los vehículos", ex);
            return new ArrayList<>();
        }
    }

    private Integer resolveAvailableStatusId(List<VehicleStatusDTO> statuses) {
        if (statuses != null) {
            for (VehicleStatusDTO status : statuses) {
                if (status == null || status.getStatusName() == null || status.getVehicleStatusId() == null) {
                    continue;
                }
                String normalized = status.getStatusName().trim().toLowerCase(Locale.ROOT);
                for (String keyword : AVAILABLE_STATUS_KEYWORDS) {
                    if (normalized.contains(keyword)) {
                        return status.getVehicleStatusId();
                    }
                }
            }
        }
        return Integer.valueOf(DEFAULT_AVAILABLE_STATUS_ID);
    }

    private VehicleCriteria buildCriteria(Map<String, String> filters,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer categoryId,
            Integer headquartersId,
            Integer statusId,
            Integer minYear,
            Integer maxYear,
            boolean onlyAvailable,
            Integer availableStatusId,
            int page,
            int pageSize) {
        VehicleCriteria criteria = new VehicleCriteria();
        criteria.setCategoryId(categoryId);
        criteria.setCurrentHeadquartersId(headquartersId);
        criteria.setDailyPriceMin(minPrice);
        criteria.setDailyPriceMax(maxPrice);
        criteria.setManufactureYearFrom(minYear);
        criteria.setManufactureYearTo(maxYear);

        String brand = filters.get(VehicleConstants.PARAM_BRAND);
        String model = filters.get(VehicleConstants.PARAM_MODEL);
        String search = filters.get(VehicleConstants.PARAM_SEARCH);

        if (brand != null) {
            criteria.setBrand(brand);
        }
        if (model != null) {
            criteria.setModel(model);
        }
        if ((brand == null || brand.isEmpty()) && search != null && !search.isEmpty()) {
            String[] parts = search.split("\\s+", 2);
            criteria.setBrand(parts[0]);
            if ((model == null || model.isEmpty()) && parts.length > 1) {
                criteria.setModel(parts[1]);
            }
        }

        if (statusId != null) {
            criteria.setVehicleStatusId(statusId);
        } else if (onlyAvailable && availableStatusId != null) {
            criteria.setVehicleStatusId(availableStatusId);
        }

        criteria.setPageNumber(Integer.valueOf(page));
        criteria.setPageSize(Integer.valueOf(pageSize));

        return criteria;
    }



    private Integer parseInteger(String rawValue, List<String> errors, String errorMessage) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(rawValue.trim());
        } catch (NumberFormatException ex) {
            errors.add(errorMessage);
            return null;
        }
    }

    private Integer parseYear(String rawValue, List<String> errors, String errorMessage) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }
        try {
            int year = Integer.parseInt(rawValue.trim());
            if (year < 1900 || year > 2100) {
                errors.add("El año debe estar entre 1900 y 2100.");
                return null;
            }
            return Integer.valueOf(year);
        } catch (NumberFormatException ex) {
            errors.add(errorMessage);
            return null;
        }
    }

    private int parsePage(String rawValue, List<String> errors) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return 1;
        }
        try {
            int parsed = Integer.parseInt(rawValue.trim());
            if (parsed < 1) {
                errors.add("La página seleccionada no es válida.");
                return 1;
            }
            return parsed;
        } catch (NumberFormatException ex) {
            errors.add("La página seleccionada no es válida.");
            return 1;
        }
    }

    private int parsePageSize(String rawValue, List<String> errors) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return 20;
        }
        try {
            int parsed = Integer.parseInt(rawValue.trim());
            if (!PAGE_SIZE_OPTIONS.contains(parsed)) {
                errors.add("El tamaño de página seleccionado no es válido.");
                return 20;
            }
            return parsed;
        } catch (NumberFormatException ex) {
            errors.add("El tamaño de página seleccionado no es válido.");
            return 20;
        }
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

    private Map<Integer, String> buildStatusNames(List<VehicleStatusDTO> statuses) {
        Map<Integer, String> names = new LinkedHashMap<Integer, String>();
        if (statuses != null) {
            for (int i = 0; i < statuses.size(); i++) {
                VehicleStatusDTO status = statuses.get(i);
                if (status != null && status.getVehicleStatusId() != null) {
                    names.put(status.getVehicleStatusId(), status.getStatusName());
                }
            }
        }
        return names;
    }

    private Results<VehicleDTO> searchVehicles(VehicleCriteria criteria, List<String> errors) {
        try {
            Results<VehicleDTO> results = vehicleService.findByCriteria(criteria);
            return normalizeResults(results, criteria);
        } catch (DataException ex) {
            LOGGER.error("Error al recuperar el catálogo público de vehículos", ex);
            if (errors != null) {
                errors.add("No se pudo cargar el catálogo en este momento. Inténtalo de nuevo más tarde.");
            }
            return normalizeResults(null, criteria);
        }
    }

    private Results<VehicleDTO> normalizeResults(Results<VehicleDTO> results, VehicleCriteria criteria) {
        if (results == null) {
            results = new Results<VehicleDTO>();
        }
        if (results.getResults() == null) {
            results.setResults(new ArrayList<VehicleDTO>());
        }
        if (criteria != null) {
            int safePage = resolveSafePage(criteria);
            int safePageSize = resolveSafePageSize(criteria);
            Integer resultPageNumber = results.getPageNumber();
            if (resultPageNumber == null || resultPageNumber.intValue() < 1) {
                results.setPageNumber(Integer.valueOf(safePage));
            }
            Integer resultPageSize = results.getPageSize();
            if (resultPageSize == null || resultPageSize.intValue() < 1) {
                results.setPageSize(Integer.valueOf(safePageSize));
            }
        }
        if (results.getTotalRecords() == null) {
            results.setTotalRecords(Integer.valueOf(results.getResults().size()));
        }
        return results;
    }

    private int resolveSafePage(VehicleCriteria criteria) {
        if (criteria == null || criteria.getPageNumber() == null) {
            return 1;
        }
        int pageNumber = criteria.getPageNumber().intValue();
        return pageNumber < 1 ? 1 : pageNumber;
    }

    private int resolveSafePageSize(VehicleCriteria criteria) {
        if (criteria == null || criteria.getPageSize() == null) {
            return 20;
        }
        int pageSize = criteria.getPageSize().intValue();
        if (!PAGE_SIZE_OPTIONS.contains(Integer.valueOf(pageSize))) {
            return 20;
        }
        return pageSize;
    }

    private Integer resolveTotalRecords(Results<VehicleDTO> results) {
        if (results == null) {
            return Integer.valueOf(0);
        }
        Integer totalRecords = results.getTotalRecords();
        if (totalRecords != null) {
            return totalRecords;
        }
        List<VehicleDTO> list = results.getResults();
        int count = list != null ? list.size() : 0;
        return Integer.valueOf(count);
    }

    private Integer resolveFromRow(Results<VehicleDTO> results) {
        Integer totalRecords = resolveTotalRecords(results);
        if (totalRecords.intValue() == 0) {
            return Integer.valueOf(0);
        }
        int pageNumber = 1;
        if (results != null && results.getPageNumber() != null && results.getPageNumber().intValue() > 0) {
            pageNumber = results.getPageNumber().intValue();
        }
        int pageSize = totalRecords.intValue();
        if (results != null && results.getPageSize() != null && results.getPageSize().intValue() > 0) {
            pageSize = results.getPageSize().intValue();
        }
        int fromRow = ((pageNumber - 1) * pageSize) + 1;
        if (fromRow > totalRecords.intValue()) {
            fromRow = totalRecords.intValue();
        }
        return Integer.valueOf(fromRow);
    }

    private Integer resolveToRow(Results<VehicleDTO> results) {
        Integer totalRecords = resolveTotalRecords(results);
        if (totalRecords.intValue() == 0) {
            return Integer.valueOf(0);
        }
        Integer fromRow = resolveFromRow(results);
        int pageSize = totalRecords.intValue();
        if (results != null && results.getPageSize() != null && results.getPageSize().intValue() > 0) {
            pageSize = results.getPageSize().intValue();
        }
        int toRow = fromRow.intValue() + pageSize - 1;
        if (toRow > totalRecords.intValue()) {
            toRow = totalRecords.intValue();
        }
        return Integer.valueOf(toRow);
    }
}
