package com.pinguela.rentexpressweb.web.public;

import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.model.VehicleStatusDTO;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.VehicleStatusService;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleStatusServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.MediaConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpres.service.HeadquartersService;
import com.pinguela.rentexpres.service.impl.HeadquartersServiceImpl;
import com.pinguela.rentexpressweb.util.ImageStorage;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.Views;
import com.pinguela.rentexpressweb.web.security.RememberMeCookies;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controlador público que renderiza la página de inicio para visitantes.
 */
@WebServlet("/public/home")
public class PublicHomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PublicHomeServlet.class);
    private static final int FEATURED_LIMIT = 4;
    private static final int DEFAULT_AVAILABLE_STATUS_ID = 1;
    private static final String[] AVAILABLE_STATUS_KEYWORDS = new String[] { "disponible", "available", "libre" };

    private final HeadquartersService headquartersService = new HeadquartersServiceImpl();
    private final VehicleService vehicleService = new VehicleServiceImpl();
    private final VehicleStatusService vehicleStatusService = new VehicleStatusServiceImpl();

    public PublicHomeServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RememberMeCookies.syncSession(request);
        exposeFlashMessage(request);

        String pageTitle = MessageResolver.getMessage(request, "home.title");
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, pageTitle);
        request.setAttribute(VehicleConstants.ATTR_HEADQUARTERS, loadHeadquarters());
        request.setAttribute(VehicleConstants.ATTR_PARAM_HEADQUARTERS, VehicleConstants.PARAM_HEADQUARTERS);
        request.setAttribute(VehicleConstants.ATTR_PARAM_PICKUP_DATE, VehicleConstants.PARAM_PICKUP_DATE);
        request.setAttribute(VehicleConstants.ATTR_PARAM_PICKUP_TIME, VehicleConstants.PARAM_PICKUP_TIME);
        request.setAttribute(VehicleConstants.ATTR_PARAM_RETURN_DATE, VehicleConstants.PARAM_RETURN_DATE);
        request.setAttribute(VehicleConstants.ATTR_PARAM_RETURN_TIME, VehicleConstants.PARAM_RETURN_TIME);

        List<VehicleDTO> featuredVehicles = loadFeaturedVehicles(request.getLocale());
        request.setAttribute(VehicleConstants.ATTR_FEATURED_VEHICLES, featuredVehicles);
        request.setAttribute(VehicleConstants.ATTR_FEATURED_VEHICLE_IMAGES,
                buildFeaturedVehicleImageMap(featuredVehicles));

        request.getRequestDispatcher(Views.PUBLIC_WELCOME).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private void exposeFlashMessage(HttpServletRequest request) {
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

    private List<HeadquartersDTO> loadHeadquarters() {
        try {
            List<HeadquartersDTO> headquarters = headquartersService.findAll();
            if (headquarters == null) {
                LOGGER.error("Error al recuperar las sedes para la portada");
                return new ArrayList<HeadquartersDTO>();
            }
            return headquarters;
        } catch (Exception ex) {
            LOGGER.error("Error al recuperar las sedes para la portada", ex);
            return new ArrayList<HeadquartersDTO>();
        }
    }

    private List<VehicleDTO> loadFeaturedVehicles(Locale locale) {
        List<VehicleDTO> featured = new ArrayList<VehicleDTO>();
        try {
            List<VehicleDTO> allVehicles = vehicleService.findAll();
            Integer availableStatusId = resolveAvailableStatusId(locale);
            if (allVehicles != null) {
                for (int i = 0; i < allVehicles.size(); i++) {
                    VehicleDTO candidate = allVehicles.get(i);
                    if (candidate == null || candidate.getVehicleId() == null) {
                        continue;
                    }
                    if (availableStatusId != null && candidate.getVehicleStatusId() != null
                            && !availableStatusId.equals(candidate.getVehicleStatusId())) {
                        continue;
                    }
                    featured.add(candidate);
                    if (featured.size() == FEATURED_LIMIT) {
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("No se pudieron cargar los vehículos destacados", ex);
        }
        return featured;
    }

    private Map<Integer, Boolean> buildFeaturedVehicleImageMap(List<VehicleDTO> vehicles) {
        Map<Integer, Boolean> imageMap = new HashMap<Integer, Boolean>();
        if (vehicles == null) {
            return imageMap;
        }
        for (int i = 0; i < vehicles.size(); i++) {
            VehicleDTO vehicle = vehicles.get(i);
            if (vehicle == null || vehicle.getVehicleId() == null) {
                continue;
            }
            File image = ImageStorage.resolveImage(getServletContext(),
                    MediaConstants.VALUE_ENTITY_VEHICLE,
                    String.valueOf(vehicle.getVehicleId()));
            if (image != null && image.exists()) {
                imageMap.put(vehicle.getVehicleId(), Boolean.TRUE);
            }
        }
        return imageMap;
    }

    private Integer resolveAvailableStatusId(Locale locale) {
        List<VehicleStatusDTO> statuses = loadVehicleStatuses(locale);
        if (statuses != null) {
            for (int i = 0; i < statuses.size(); i++) {
                VehicleStatusDTO status = statuses.get(i);
                if (status == null || status.getStatusName() == null || status.getVehicleStatusId() == null) {
                    continue;
                }
                String normalized = status.getStatusName().trim().toLowerCase(Locale.ROOT);
                for (int j = 0; j < AVAILABLE_STATUS_KEYWORDS.length; j++) {
                    String keyword = AVAILABLE_STATUS_KEYWORDS[j];
                    if (keyword != null && normalized.contains(keyword)) {
                        return status.getVehicleStatusId();
                    }
                }
            }
        }
        return Integer.valueOf(DEFAULT_AVAILABLE_STATUS_ID);
    }

    private List<VehicleStatusDTO> loadVehicleStatuses(Locale locale) {
        List<VehicleStatusDTO> statuses = new ArrayList<VehicleStatusDTO>();
        try {
            String language = locale != null ? locale.getLanguage() : Locale.getDefault().getLanguage();
            List<VehicleStatusDTO> loaded = vehicleStatusService.findAll(language);
            if (loaded != null) {
                statuses.addAll(loaded);
            }
        } catch (Exception ex) {
            LOGGER.warn("No se pudieron recuperar los estados de los vehículos", ex);
        }
        return statuses;
    }
}
