package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.model.VehicleCriteria;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.HeadquartersService;
import com.pinguela.rentexpres.service.VehicleCategoryService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.impl.HeadquartersServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleCategoryServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.ReservationConstants;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/VehicleServlet")
public class PublicVehicleServlet extends BasePrivateServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(PublicVehicleServlet.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

    private final VehicleService vehicleService = new VehicleServiceImpl();
    private final VehicleCategoryService categoryService = new VehicleCategoryServiceImpl();
    private final HeadquartersService headquartersService = new HeadquartersServiceImpl();

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 9;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        configureEncoding(req, res);

        String action = req.getParameter(AppConstants.ACTION);

        try {
            /*
             * DETALLE DE VEHÍCULO
             */
            if (Objects.equals(action, AppConstants.VIEW_VEHICLE_DETAIL)) {

                String id = req.getParameter(ReservationConstants.PARAM_VEHICLE_ID);
                VehicleDTO vehicle = null;

                if (id != null && !id.isEmpty()) {
                    vehicle = vehicleService.findById(Integer.valueOf(id));
                }

                req.setAttribute("vehicle", vehicle);
                req.setAttribute("cartVehicle", SessionManager.getAttribute(req, ReservationConstants.ATTR_CART_VEHICLE));
                String language = resolveLanguage(req);
                req.setAttribute("categories", categoryService.findAll(language));
                req.setAttribute("headquarters", headquartersService.findAll());

                req.getRequestDispatcher(Views.PUBLIC_VEHICLE_DETAIL).forward(req, res);
                return;
            }

            /*
             * LISTADO PÚBLICO CON FILTROS
             */
            VehicleCriteria c = new VehicleCriteria();
            c.setPageNumber(DEFAULT_PAGE);
            c.setPageSize(DEFAULT_SIZE);

            String v;

            v = req.getParameter(AppConstants.PARAM_PAGE);
            if (v != null && !v.isEmpty()) c.setPageNumber(Integer.valueOf(v));

            v = req.getParameter(AppConstants.PARAM_PAGE_SIZE);
            if (v != null && !v.isEmpty()) c.setPageSize(Integer.valueOf(v));

            v = req.getParameter(AppConstants.PARAM_SEARCH);
            if (v != null && !v.isEmpty()) c.setBrand(v);

            v = req.getParameter(AppConstants.PARAM_CATEGORY_ID);
            if (v != null && !v.isEmpty()) c.setCategoryId(Integer.valueOf(v));

            v = req.getParameter(AppConstants.PARAM_MODEL);
            if (v != null && !v.isEmpty()) c.setModel(v);

            v = req.getParameter(AppConstants.PARAM_HEADQUARTERS_ID);
            if (v != null && !v.isEmpty()) c.setCurrentHeadquartersId(Integer.valueOf(v));

            v = req.getParameter(AppConstants.PARAM_MIN_PRICE);
            if (v != null && !v.isEmpty()) c.setDailyPriceMin(new BigDecimal(v));

            v = req.getParameter(AppConstants.PARAM_MAX_PRICE);
            if (v != null && !v.isEmpty()) c.setDailyPriceMax(new BigDecimal(v));

            v = req.getParameter(AppConstants.PARAM_YEAR_FROM);
            if (v != null && !v.isEmpty()) c.setManufactureYearFrom(Integer.valueOf(v));

            v = req.getParameter(AppConstants.PARAM_YEAR_TO);
            if (v != null && !v.isEmpty()) c.setManufactureYearTo(Integer.valueOf(v));

            v = req.getParameter(AppConstants.PARAM_MILEAGE_MIN);
            if (v != null && !v.isEmpty()) c.setCurrentMileageMin(Integer.valueOf(v));

            v = req.getParameter(AppConstants.PARAM_MILEAGE_MAX);
            if (v != null && !v.isEmpty()) c.setCurrentMileageMax(Integer.valueOf(v));

            Results<VehicleDTO> results = vehicleService.findByCriteria(c);

            String language = resolveLanguage(req);

            req.setAttribute("criteria", c);
            req.setAttribute("results", results);
            req.setAttribute("vehicles", results.getResults());

            req.setAttribute("categories", categoryService.findAll(language));
            req.setAttribute("headquarters", headquartersService.findAll());
            req.setAttribute("cartVehicle", SessionManager.getAttribute(req, ReservationConstants.ATTR_CART_VEHICLE));

            req.getRequestDispatcher(Views.PUBLIC_VEHICLE_LIST).forward(req, res);

        } catch (Exception ex) {
            logger.error("Error in public vehicle servlet", ex);
            throw new ServletException("Error processing public vehicles", ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        configureEncoding(req, res);

        String action = req.getParameter(AppConstants.ACTION);

        try {

            /*
             * AÑADIR AL CARRITO
             */
            if ("addVehicleToCart".equals(action)) {

                String id = req.getParameter(ReservationConstants.PARAM_VEHICLE_ID);
                if (id != null && !id.isEmpty()) {
                    VehicleDTO v = vehicleService.findById(Integer.valueOf(id));
                    SessionManager.set(req, ReservationConstants.ATTR_CART_VEHICLE, v);
                }

            /*
             * LIBERAR CARRITO
             */
            } else if ("releaseVehicle".equals(action)) {
                SessionManager.remove(req, ReservationConstants.ATTR_CART_VEHICLE);
            }

            /*
             * CARGAR LISTA POR DEFECTO
             */
            VehicleCriteria c = new VehicleCriteria();
            c.setPageNumber(DEFAULT_PAGE);
            c.setPageSize(DEFAULT_SIZE);

            Results<VehicleDTO> results = vehicleService.findByCriteria(c);

            String language = resolveLanguage(req);

            req.setAttribute("criteria", c);
            req.setAttribute("results", results);
            req.setAttribute("vehicles", results.getResults());
            req.setAttribute("categories", categoryService.findAll(language));
            req.setAttribute("headquarters", headquartersService.findAll());
            req.setAttribute("cartVehicle", SessionManager.getAttribute(req, ReservationConstants.ATTR_CART_VEHICLE));

            req.getRequestDispatcher(Views.PUBLIC_VEHICLE_LIST).forward(req, res);

        } catch (Exception ex) {
            logger.error("Error processing public vehicle POST", ex);
            throw new ServletException("Unable to process request", ex);
        }
    }
}
