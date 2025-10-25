package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

import com.pinguela.rentexpres.dao.HeadquartersDAO;
import com.pinguela.rentexpres.dao.impl.HeadquartersDAOImpl;
import com.pinguela.rentexpres.model.VehicleCriteria;
import com.pinguela.rentexpres.service.FileService;
import com.pinguela.rentexpres.service.VehicleCategoryService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.VehicleStatusService;
import com.pinguela.rentexpres.service.impl.FileServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleCategoryServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleStatusServiceImpl;
import com.pinguela.rentexpressweb.util.Views;
import com.pinguela.rentexpressweb.service.HeadquartersLookupService;
import com.pinguela.rentexpressweb.service.VehiclePresentationService;
import com.pinguela.rentexpressweb.service.VehiclePresentationService.VehicleListData;

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

        private final VehiclePresentationService vehiclePresentationService;

        public PublicVehicleServlet() {
                this(new VehicleServiceImpl(), new FileServiceImpl(), new VehicleCategoryServiceImpl(),
                                new VehicleStatusServiceImpl(), new HeadquartersDAOImpl());
        }

        PublicVehicleServlet(VehicleService vehicleService, FileService fileService,
                        VehicleCategoryService vehicleCategoryService, VehicleStatusService vehicleStatusService,
                        HeadquartersDAO headquartersDAO) {
                HeadquartersLookupService headquartersLookupService = new HeadquartersLookupService(headquartersDAO);
                this.vehiclePresentationService = new VehiclePresentationService(vehicleService, fileService,
                                vehicleCategoryService, vehicleStatusService, headquartersLookupService);
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

                String language = resolveLanguage(request.getSession());
                VehicleListData listData = vehiclePresentationService.loadVehicleList(criteria, language);

                request.setAttribute("vehicleCriteria", criteria);
                request.setAttribute("vehicleResults", listData.getResults());
                request.setAttribute("vehicleList", listData.getVehicles());
                request.setAttribute("vehicleImages", listData.getVehicleImages());
                request.setAttribute("vehicleCategories", listData.getCategories());
                request.setAttribute("vehicleStatuses", listData.getStatuses());
                request.setAttribute("vehicleListError", Boolean.valueOf(listData.hasErrors()));

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
