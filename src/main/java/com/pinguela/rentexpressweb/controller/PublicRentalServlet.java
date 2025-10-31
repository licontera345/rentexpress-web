package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.model.RentalDTO;
import com.pinguela.rentexpres.model.RentalStatusDTO;
import com.pinguela.rentexpres.service.RentalService;
import com.pinguela.rentexpres.service.RentalStatusService;
import com.pinguela.rentexpres.service.impl.RentalServiceImpl;
import com.pinguela.rentexpres.service.impl.RentalStatusServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.RentalConstants;
import com.pinguela.rentexpressweb.util.SessionUtils;
import com.pinguela.rentexpressweb.util.LegacyDateUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Muestra un informe público con los alquileres activos y finalizados.
 */
@WebServlet("/public/rentals")
public class PublicRentalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PublicRentalServlet.class);
    private static final String DISPLAY_DATE_PATTERN = "dd/MM/yyyy";

    private final RentalService rentalService = new RentalServiceImpl();
    private final RentalStatusService rentalStatusService = new RentalStatusServiceImpl();

    public PublicRentalServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (SessionUtils.getAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE) == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Actividad de alquileres");

        Map<String, String> filters = buildFilters(request);
        List<String> errors = new ArrayList<>();
        Locale locale = request.getLocale();

        Integer statusId = parseInteger(filters.get(RentalConstants.PARAM_STATUS), errors,
                "El estado seleccionado no es válido.");
        Date startFrom = parseDate(filters.get(RentalConstants.PARAM_START_FROM), errors,
                "La fecha de inicio " + "desde" + " no es válida.");
        Date startTo = parseDate(filters.get(RentalConstants.PARAM_START_TO), errors,
                "La fecha de inicio " + "hasta" + " no es válida.");

        if (startFrom != null && startTo != null && startFrom.after(startTo)) {
            errors.add("El rango de fechas no es coherente.");
        }

        BigDecimal minCost = parseDecimal(filters.get(RentalConstants.PARAM_MIN_COST), errors,
                "El importe mínimo no es válido.");
        BigDecimal maxCost = parseDecimal(filters.get(RentalConstants.PARAM_MAX_COST), errors,
                "El importe máximo no es válido.");

        if (minCost != null && maxCost != null && minCost.compareTo(maxCost) > 0) {
            errors.add("El importe mínimo no puede superar al máximo.");
        }

        List<RentalDTO> rentals = loadRentals();
        List<RentalStatusDTO> statusOptions = loadStatuses(locale);
        Map<Integer, String> statusNames = mapStatusNames(statusOptions);

        List<RentalDTO> filtered = filterRentals(rentals, statusId, startFrom, startTo, minCost, maxCost);
        Map<Integer, Long> statusCounts = countByStatus(filtered, statusOptions);
        Map<String, Object> summary = buildSummary(filtered);
        List<RentalDTO> latestRentals = buildLatestRentals(filtered, 5);

        request.setAttribute(RentalConstants.ATTR_RENTAL_FILTERS, filters);
        request.setAttribute(RentalConstants.ATTR_RENTAL_ERRORS, errors);
        request.setAttribute(RentalConstants.ATTR_STATUS_OPTIONS, statusOptions);
        request.setAttribute(RentalConstants.ATTR_STATUS_NAMES, statusNames);
        request.setAttribute(RentalConstants.ATTR_STATUS_COUNTS, statusCounts);
        request.setAttribute(RentalConstants.ATTR_RENTALS, buildRentalViews(filtered, statusNames));
        request.setAttribute(RentalConstants.ATTR_LATEST_RENTALS, buildRentalViews(latestRentals, statusNames));
        request.setAttribute(RentalConstants.ATTR_RENTAL_SUMMARY, summary);
        exposeParameterNames(request);
        request.getRequestDispatcher("/public/rental/rentals_report.jsp").forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private Map<String, String> buildFilters(HttpServletRequest request) {
        Map<String, String> filters = new HashMap<>();
        filters.put(RentalConstants.PARAM_STATUS, sanitize(request.getParameter(RentalConstants.PARAM_STATUS)));
        filters.put(RentalConstants.PARAM_START_FROM, sanitize(request.getParameter(RentalConstants.PARAM_START_FROM)));
        filters.put(RentalConstants.PARAM_START_TO, sanitize(request.getParameter(RentalConstants.PARAM_START_TO)));
        filters.put(RentalConstants.PARAM_MIN_COST, sanitize(request.getParameter(RentalConstants.PARAM_MIN_COST)));
        filters.put(RentalConstants.PARAM_MAX_COST, sanitize(request.getParameter(RentalConstants.PARAM_MAX_COST)));
        return filters;
    }

    private void exposeParameterNames(HttpServletRequest request) {
        request.setAttribute(RentalConstants.ATTR_PARAM_ACTION, RentalConstants.PARAM_ACTION);
        request.setAttribute(RentalConstants.ATTR_PARAM_STATUS, RentalConstants.PARAM_STATUS);
        request.setAttribute(RentalConstants.ATTR_PARAM_START_FROM, RentalConstants.PARAM_START_FROM);
        request.setAttribute(RentalConstants.ATTR_PARAM_START_TO, RentalConstants.PARAM_START_TO);
        request.setAttribute(RentalConstants.ATTR_PARAM_MIN_COST, RentalConstants.PARAM_MIN_COST);
        request.setAttribute(RentalConstants.ATTR_PARAM_MAX_COST, RentalConstants.PARAM_MAX_COST);
    }

    private String sanitize(String value) {
        return value != null ? value.trim() : null;
    }

    private Integer parseInteger(String value, List<String> errors, String message) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            errors.add(message);
            return null;
        }
    }

    private Date parseDate(String value, List<String> errors, String message) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        Date parsed = LegacyDateUtils.parseIsoDate(value);
        if (parsed == null) {
            errors.add(message);
        }
        return parsed;
    }

    private BigDecimal parseDecimal(String value, List<String> errors, String message) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            BigDecimal decimal = new BigDecimal(value);
            if (decimal.compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Los importes deben ser positivos.");
                return null;
            }
            return decimal;
        } catch (NumberFormatException ex) {
            errors.add(message);
            return null;
        }
    }

    private List<RentalDTO> loadRentals() {
        try {
            return rentalService.findAll();
        } catch (Exception ex) {
            LOGGER.error("No se pudieron recuperar los alquileres", ex);
            return Collections.emptyList();
        }
    }

    private List<RentalStatusDTO> loadStatuses(Locale locale) {
        try {
            String language = locale != null ? locale.getLanguage() : Locale.getDefault().getLanguage();
            return rentalStatusService.findAll(language);
        } catch (Exception ex) {
            LOGGER.warn("No se pudieron recuperar los estados de alquiler", ex);
            return Collections.emptyList();
        }
    }

    private Map<Integer, String> mapStatusNames(List<RentalStatusDTO> statuses) {
        Map<Integer, String> names = new LinkedHashMap<>();
        for (RentalStatusDTO status : statuses) {
            if (status != null && status.getRentalStatusId() != null) {
                names.put(status.getRentalStatusId(), status.getStatusName());
            }
        }
        return names;
    }

    private List<RentalDTO> filterRentals(List<RentalDTO> rentals, Integer statusId, Date startFrom, Date startTo,
            BigDecimal minCost, BigDecimal maxCost) {
        List<RentalDTO> filtered = new ArrayList<>();
        for (RentalDTO rental : rentals) {
            if (rental == null) {
                continue;
            }
            if (statusId != null && !Objects.equals(rental.getRentalStatusId(), statusId)) {
                continue;
            }
            if (!matchesStartDate(rental, startFrom, startTo)) {
                continue;
            }
            if (!matchesCost(rental, minCost, maxCost)) {
                continue;
            }
            filtered.add(rental);
        }
        sortByStartDateDescending(filtered);
        return filtered;
    }

    private boolean matchesStartDate(RentalDTO rental, Date startFrom, Date startTo) {
        if (startFrom == null && startTo == null) {
            return true;
        }
        if (rental.getStartDateEffective() == null) {
            return false;
        }
        Date startDate = LegacyDateUtils.toDate(rental.getStartDateEffective());
        if (startDate == null) {
            return false;
        }
        if (startFrom != null && startDate.before(startFrom)) {
            return false;
        }
        if (startTo != null && startDate.after(startTo)) {
            return false;
        }
        return true;
    }

    private boolean matchesCost(RentalDTO rental, BigDecimal minCost, BigDecimal maxCost) {
        BigDecimal total = rental.getTotalCost();
        if (total == null) {
            return minCost == null && maxCost == null;
        }
        if (minCost != null && total.compareTo(minCost) < 0) {
            return false;
        }
        if (maxCost != null && total.compareTo(maxCost) > 0) {
            return false;
        }
        return true;
    }

    private Map<Integer, Long> countByStatus(List<RentalDTO> rentals, List<RentalStatusDTO> statuses) {
        Map<Integer, Long> counts = new LinkedHashMap<>();
        for (RentalStatusDTO status : statuses) {
            if (status != null && status.getRentalStatusId() != null) {
                long count = 0L;
                for (RentalDTO rental : rentals) {
                    if (rental != null
                            && Objects.equals(rental.getRentalStatusId(), status.getRentalStatusId())) {
                        count++;
                    }
                }
                counts.put(status.getRentalStatusId(), Long.valueOf(count));
            }
        }
        return counts;
    }

    private Map<String, Object> buildSummary(List<RentalDTO> rentals) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRentals", Integer.valueOf(rentals.size()));

        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (RentalDTO rental : rentals) {
            if (rental != null && rental.getTotalCost() != null) {
                totalRevenue = totalRevenue.add(rental.getTotalCost());
            }
        }
        summary.put("totalRevenue", totalRevenue);

        long totalDays = 0L;
        for (RentalDTO rental : rentals) {
            if (rental != null) {
                totalDays += calculateDays(rental.getStartDateEffective(), rental.getEndDateEffective());
            }
        }
        double averageDuration = rentals.isEmpty() ? 0d : (double) totalDays / rentals.size();
        summary.put("averageDuration", Double.valueOf(averageDuration));

        BigDecimal averageTicket = rentals.isEmpty() ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(rentals.size()), 2, RoundingMode.HALF_UP);
        summary.put("averageTicket", averageTicket);
        return summary;
    }

    private long calculateDays(Object start, Object end) {
        Date startDate = LegacyDateUtils.toDate(start);
        Date endDate = LegacyDateUtils.toDate(end);
        return LegacyDateUtils.daysBetween(startDate, endDate);
    }

    private List<Map<String, Object>> buildRentalViews(List<RentalDTO> rentals, Map<Integer, String> statusNames) {
        List<Map<String, Object>> views = new ArrayList<>();
        for (RentalDTO rental : rentals) {
            if (rental == null) {
                continue;
            }
            Map<String, Object> view = new HashMap<>();
            view.put("id", rental.getRentalId());
            view.put("brand", rental.getBrand());
            view.put("model", rental.getModel());
            view.put("licensePlate", rental.getLicensePlate());
            view.put("statusId", rental.getRentalStatusId());
            view.put("status", statusNames.get(rental.getRentalStatusId()));
            view.put("start", formatDate(rental.getStartDateEffective()));
            view.put("end", formatDate(rental.getEndDateEffective()));
            view.put("totalCost", rental.getTotalCost());
            views.add(view);
        }
        return views;
    }

    private void sortByStartDateDescending(List<RentalDTO> rentals) {
        Collections.sort(rentals, new Comparator<RentalDTO>() {
            @Override
            public int compare(RentalDTO first, RentalDTO second) {
                Date firstDate = first != null ? LegacyDateUtils.toDate(first.getStartDateEffective()) : null;
                Date secondDate = second != null ? LegacyDateUtils.toDate(second.getStartDateEffective()) : null;
                if (firstDate == null && secondDate == null) {
                    return 0;
                }
                if (firstDate == null) {
                    return 1;
                }
                if (secondDate == null) {
                    return -1;
                }
                return secondDate.compareTo(firstDate);
            }
        });
    }

    private List<RentalDTO> buildLatestRentals(List<RentalDTO> rentals, int limit) {
        List<RentalDTO> latest = new ArrayList<>(rentals);
        sortByStartDateDescending(latest);
        if (latest.size() > limit) {
            return new ArrayList<>(latest.subList(0, limit));
        }
        return latest;
    }

    private String formatDate(Object value) {
        Date date = LegacyDateUtils.toDate(value);
        if (date == null) {
            return "-";
        }
        return LegacyDateUtils.formatDate(date, DISPLAY_DATE_PATTERN);
    }
}
