package com.pinguela.rentexpressweb.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.model.VehicleCategoryDTO;
import com.pinguela.rentexpres.model.VehicleCriteria;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.model.VehicleStatusDTO;
import com.pinguela.rentexpres.service.FileService;
import com.pinguela.rentexpres.service.VehicleCategoryService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.VehicleStatusService;
import com.pinguela.rentexpres.service.impl.FileServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleCategoryServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleStatusServiceImpl;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/public/VehicleServlet")
public class PublicVehicleServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;

        private static final String ACTION_LIST = "list";

        private final VehicleService vehicleService;
        private final FileService fileService;
        private final VehicleCategoryService vehicleCategoryService;
        private final VehicleStatusService vehicleStatusService;

        public PublicVehicleServlet() {
                this(new VehicleServiceImpl(), new FileServiceImpl(), new VehicleCategoryServiceImpl(),
                                new VehicleStatusServiceImpl());
        }

        PublicVehicleServlet(VehicleService vehicleService, FileService fileService,
                        VehicleCategoryService vehicleCategoryService, VehicleStatusService vehicleStatusService) {
                this.vehicleService = vehicleService;
                this.fileService = fileService;
                this.vehicleCategoryService = vehicleCategoryService;
                this.vehicleStatusService = vehicleStatusService;
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {
                String action = request.getParameter("action");
                if (action == null || action.isBlank()) {
                        action = ACTION_LIST;
                }

                if (ACTION_LIST.equals(action)) {
                        handleList(request, response);
                } else {
                        response.sendRedirect(request.getContextPath() + Views.INDEX);
                }
        }

        private void handleList(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {
                VehicleCriteria criteria = buildCriteria(request);

                Results<VehicleDTO> results = null;
                List<VehicleDTO> vehicles = Collections.emptyList();
                Map<Integer, Boolean> vehicleImages = Collections.emptyMap();
                boolean hasErrors = false;

                try {
                        results = vehicleService.findByCriteria(criteria);
                        if (results == null) {
                                results = new Results<>();
                        }
                        results.setPage(criteria.getSafePage());
                        results.setPageSize(criteria.getSafePageSize());
                        results.normalize();
                        vehicles = results.getResults();
                        if (vehicles == null) {
                                vehicles = Collections.emptyList();
                        }

                        vehicleImages = buildVehicleImages(vehicles);
                } catch (RentexpresException e) {
                        hasErrors = true;
                        results = new Results<>();
                        results.setPage(criteria.getSafePage());
                        results.setPageSize(criteria.getSafePageSize());
                        results.normalize();
                        vehicles = Collections.emptyList();
                        vehicleImages = Collections.emptyMap();
                }

                String language = resolveLanguage(request.getSession());
                List<VehicleCategoryDTO> categories = loadCategories(language);
                List<VehicleStatusDTO> statuses = loadStatuses(language);

                request.setAttribute("vehicleCriteria", criteria);
                request.setAttribute("vehicleResults", results);
                request.setAttribute("vehicleList", vehicles);
                request.setAttribute("vehicleImages", vehicleImages);
                request.setAttribute("vehicleCategories", categories);
                request.setAttribute("vehicleStatuses", statuses);
                request.setAttribute("vehicleListError", Boolean.valueOf(hasErrors));

                request.getRequestDispatcher(Views.VEHICLE_LIST).forward(request, response);
        }

        private VehicleCriteria buildCriteria(HttpServletRequest request) {
                VehicleCriteria criteria = new VehicleCriteria();
                criteria.setPageNumber(parseInteger(request.getParameter("page"), 1));
                criteria.setPageSize(Integer.valueOf(20));
                criteria.setOrderBy("created_at");
                criteria.setOrderDir("DESC");

                criteria.setBrand(request.getParameter("brand"));
                criteria.setModel(request.getParameter("model"));
                criteria.setCategoryId(parseInteger(request.getParameter("categoryId"), null));
                criteria.setVehicleStatusId(parseInteger(request.getParameter("vehicleStatusId"), null));
                criteria.setDailyPriceMin(parseBigDecimal(request.getParameter("priceMin")));
                criteria.setDailyPriceMax(parseBigDecimal(request.getParameter("priceMax")));

                criteria.normalize();
                return criteria;
        }

        private Map<Integer, Boolean> buildVehicleImages(List<VehicleDTO> vehicles) throws RentexpresException {
                if (vehicles == null || vehicles.isEmpty()) {
                        return Collections.emptyMap();
                }

                Map<Integer, Boolean> images = new HashMap<>();
                for (VehicleDTO vehicle : vehicles) {
                        if (vehicle == null || vehicle.getVehicleId() == null) {
                                continue;
                        }

                        List<File> files = fileService.getImagesByVehicleId(vehicle.getVehicleId());
                        if (files != null && !files.isEmpty()) {
                                images.put(vehicle.getVehicleId(), Boolean.TRUE);
                        }
                }
                return images;
        }

        private String resolveLanguage(HttpSession session) {
                if (session != null) {
                        Object localeAttr = session.getAttribute("locale");
                        if (localeAttr instanceof Locale) {
                                Locale locale = (Locale) localeAttr;
                                if (locale.getLanguage() != null && !locale.getLanguage().isEmpty()) {
                                        return locale.getLanguage();
                                }
                        }
                }
                return Locale.getDefault().getLanguage();
        }

        private List<VehicleCategoryDTO> loadCategories(String language) {
                try {
                        List<VehicleCategoryDTO> categories = vehicleCategoryService.findAll(language);
                        return categories != null ? categories : Collections.emptyList();
                } catch (RentexpresException e) {
                        return Collections.emptyList();
                }
        }

        private List<VehicleStatusDTO> loadStatuses(String language) {
                try {
                        List<VehicleStatusDTO> statuses = vehicleStatusService.findAll(language);
                        return statuses != null ? statuses : Collections.emptyList();
                } catch (RentexpresException e) {
                        return Collections.emptyList();
                }
        }

        private Integer parseInteger(String value, Integer defaultValue) {
                if (value == null || value.isBlank()) {
                        return defaultValue;
                }
                try {
                        return Integer.valueOf(value.trim());
                } catch (NumberFormatException e) {
                        return defaultValue;
                }
        }

        private BigDecimal parseBigDecimal(String value) {
                if (value == null || value.isBlank()) {
                        return null;
                }
                try {
                        return new BigDecimal(value.trim());
                } catch (NumberFormatException e) {
                        return null;
                }
        }
}
