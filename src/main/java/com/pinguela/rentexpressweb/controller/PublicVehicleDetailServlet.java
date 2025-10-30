package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.VehicleCategoryDTO;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.VehicleCategoryService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.impl.VehicleCategoryServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.ReservationConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpres.service.HeadquartersService;
import com.pinguela.rentexpres.service.impl.HeadquartersServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Muestra el detalle de un vehículo del catálogo público.
 */
@WebServlet("/public/vehicles/detail")
public class PublicVehicleDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PublicVehicleDetailServlet.class);

    private final VehicleService vehicleService = new VehicleServiceImpl();
    private final VehicleCategoryService categoryService = new VehicleCategoryServiceImpl();
    private final HeadquartersService headquartersService = new HeadquartersServiceImpl();

    public PublicVehicleDetailServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String vehicleIdParam = request.getParameter(VehicleConstants.PARAM_VEHICLE_ID);
        Integer vehicleId = parseVehicleId(vehicleIdParam);
        if (vehicleId == null) {
            response.sendRedirect(request.getContextPath() + "/public/vehicles?notfound=1");
            return;
        }

        VehicleDTO vehicle = loadVehicle(vehicleId);
        if (vehicle == null) {
            response.sendRedirect(request.getContextPath() + "/public/vehicles?notfound=1");
            return;
        }

        Locale locale = request.getLocale();
        String categoryName = resolveCategoryName(vehicle.getCategoryId(), locale);

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, vehicle.getBrand() + " " + vehicle.getModel());
        request.setAttribute(VehicleConstants.ATTR_SELECTED_VEHICLE, vehicle);
        request.setAttribute(VehicleConstants.ATTR_SELECTED_CATEGORY_NAME, categoryName);

        List<VehicleDTO> relatedVehicles = findRelatedVehicles(vehicle);
        request.setAttribute(VehicleConstants.ATTR_RELATED_VEHICLES, relatedVehicles);

        request.setAttribute(ReservationConstants.ATTR_HEADQUARTERS, loadHeadquarters());

        if (request.getAttribute(ReservationConstants.ATTR_RESERVATION_FORM) == null) {
            request.setAttribute(ReservationConstants.ATTR_RESERVATION_FORM,
                    buildDefaultForm(request, vehicleIdParam));
        }

        exposeReservationParameterNames(request);

        request.getRequestDispatcher("/public/vehicle/vehicle_detail.jsp").forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private Integer parseVehicleId(String vehicleIdParam) {
        if (vehicleIdParam == null) {
            return null;
        }
        try {
            return Integer.valueOf(vehicleIdParam);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private VehicleDTO loadVehicle(Integer vehicleId) {
        try {
            return vehicleService.findById(vehicleId);
        } catch (RentexpresException ex) {
            LOGGER.error("Error al recuperar el vehículo {}", vehicleId, ex);
            return null;
        }
    }

    private String resolveCategoryName(Integer categoryId, Locale locale) {
        if (categoryId == null) {
            return null;
        }
        try {
            String language = locale != null ? locale.getLanguage() : Locale.getDefault().getLanguage();
            VehicleCategoryDTO dto = categoryService.findById(categoryId, language);
            return dto != null ? dto.getCategoryName() : null;
        } catch (RentexpresException ex) {
            LOGGER.warn("No se pudo obtener el nombre de la categoría {}", categoryId, ex);
            return null;
        }
    }

    private List<VehicleDTO> findRelatedVehicles(VehicleDTO vehicle) {
        try {
            List<VehicleDTO> related = new ArrayList<>();
            List<VehicleDTO> allVehicles = vehicleService.findAll();
            for (VehicleDTO other : allVehicles) {
                if (other == null || other.getVehicleId() == null
                        || other.getVehicleId().equals(vehicle.getVehicleId())) {
                    continue;
                }
                if (vehicle.getCategoryId() != null && vehicle.getCategoryId().equals(other.getCategoryId())) {
                    related.add(other);
                    if (related.size() == 3) {
                        break;
                    }
                }
            }
            return related;
        } catch (RentexpresException ex) {
            LOGGER.warn("No se pudieron cargar vehículos relacionados", ex);
            return new ArrayList<>();
        }
    }

    private List<HeadquartersDTO> loadHeadquarters() {
        try {
            List<HeadquartersDTO> headquarters = headquartersService.findAll();
            if (headquarters == null) {
                LOGGER.error("Error al recuperar las sedes");
                return new ArrayList<HeadquartersDTO>();
            }
            return headquarters;
        } catch (RentexpresException ex) {
            LOGGER.error("Error al recuperar las sedes", ex);
            return new ArrayList<HeadquartersDTO>();
        }
    }

    private Map<String, String> buildDefaultForm(HttpServletRequest request, String vehicleIdParam) {
        Map<String, String> defaults = new HashMap<>();
        defaults.put(ReservationConstants.PARAM_VEHICLE_ID, vehicleIdParam);
        String pickupDate = firstNonEmpty(
                request.getParameter(ReservationConstants.PARAM_START_DATE),
                request.getParameter(VehicleConstants.PARAM_PICKUP_DATE));
        if (pickupDate != null) {
            defaults.put(ReservationConstants.PARAM_START_DATE, pickupDate);
        }

        String returnDate = firstNonEmpty(
                request.getParameter(ReservationConstants.PARAM_END_DATE),
                request.getParameter(VehicleConstants.PARAM_RETURN_DATE));
        if (returnDate != null) {
            defaults.put(ReservationConstants.PARAM_END_DATE, returnDate);
        }

        String pickupHeadquarters = firstNonEmpty(
                request.getParameter(ReservationConstants.PARAM_PICKUP_HEADQUARTERS),
                request.getParameter(VehicleConstants.PARAM_HEADQUARTERS));
        if (pickupHeadquarters != null) {
            defaults.put(ReservationConstants.PARAM_PICKUP_HEADQUARTERS, pickupHeadquarters);
        }

        String returnHeadquarters = firstNonEmpty(
                request.getParameter(ReservationConstants.PARAM_RETURN_HEADQUARTERS));
        if (returnHeadquarters != null) {
            defaults.put(ReservationConstants.PARAM_RETURN_HEADQUARTERS, returnHeadquarters);
        }
        return defaults;
    }

    private void exposeReservationParameterNames(HttpServletRequest request) {
        request.setAttribute(ReservationConstants.ATTR_PARAM_VEHICLE_ID, ReservationConstants.PARAM_VEHICLE_ID);
        request.setAttribute(ReservationConstants.ATTR_PARAM_START_DATE, ReservationConstants.PARAM_START_DATE);
        request.setAttribute(ReservationConstants.ATTR_PARAM_END_DATE, ReservationConstants.PARAM_END_DATE);
        request.setAttribute(ReservationConstants.ATTR_PARAM_PICKUP_HEADQUARTERS,
                ReservationConstants.PARAM_PICKUP_HEADQUARTERS);
        request.setAttribute(ReservationConstants.ATTR_PARAM_RETURN_HEADQUARTERS,
                ReservationConstants.PARAM_RETURN_HEADQUARTERS);
    }

    private String firstNonEmpty(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            String sanitized = sanitize(value);
            if (sanitized != null && !sanitized.isEmpty()) {
                return sanitized;
            }
        }
        return null;
    }

    private String sanitize(String value) {
        return value != null ? value.trim() : null;
    }
}
