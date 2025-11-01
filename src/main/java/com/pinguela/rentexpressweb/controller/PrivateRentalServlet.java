package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.RentalCriteria;
import com.pinguela.rentexpres.model.RentalDTO;
import com.pinguela.rentexpres.model.ReservationDTO;
import com.pinguela.rentexpres.service.RentalService;
import com.pinguela.rentexpres.service.ReservationService;
import com.pinguela.rentexpres.service.impl.RentalServiceImpl;
import com.pinguela.rentexpres.service.impl.ReservationServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.RentalConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/rentals")
public class PrivateRentalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PrivateRentalServlet.class);

    private transient RentalService rentalService;
    private transient ReservationService reservationService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.rentalService = new RentalServiceImpl();
        this.reservationService = new ReservationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (SessionManager.get(request, AppConstants.ATTR_CURRENT_USER) == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        RentalCriteria criteria = new RentalCriteria();
        try {
            List<RentalDTO> rentals = Collections.emptyList();
            var results = rentalService.findByCriteria(criteria);
            if (results != null && results.getResults() != null) {
                rentals = results.getResults();
            }
            request.setAttribute(RentalConstants.ATTR_RENTALS, rentals);
        } catch (RentexpresException ex) {
            LOGGER.error("Unable to load rentals", ex);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
            request.setAttribute(RentalConstants.ATTR_RENTALS, Collections.emptyList());
        }
        request.getRequestDispatcher(Views.PRIVATE_RENTAL_DASHBOARD).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (SessionManager.get(request, AppConstants.ATTR_CURRENT_EMPLOYEE) == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }

        String action = request.getParameter(RentalConstants.PARAM_ACTION);
        if (!"convert".equalsIgnoreCase(action)) {
            doGet(request, response);
            return;
        }

        String reservationIdParam = request.getParameter("reservationId");
        Integer reservationId = parseInteger(reservationIdParam);
        if (reservationId == null) {
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "error.validation.reservationRequired"));
            doGet(request, response);
            return;
        }

        try {
            if (rentalService.existsByReservation(reservationId)) {
                request.setAttribute(AppConstants.ATTR_FLASH_ERROR,
                        MessageResolver.getMessage(request, "error.validation.rentalAlreadyExists"));
                doGet(request, response);
                return;
            }

            ReservationDTO reservation = reservationService.findById(reservationId);
            if (reservation == null) {
                request.setAttribute(AppConstants.ATTR_FLASH_ERROR,
                        MessageResolver.getMessage(request, "error.validation.reservationNotFound"));
                doGet(request, response);
                return;
            }

            rentalService.createFromReservation(reservation);
            request.setAttribute(AppConstants.ATTR_FLASH_SUCCESS,
                    MessageResolver.getMessage(request, "rental.flash.conversionSuccess"));
        } catch (RentexpresException ex) {
            LOGGER.error("Error converting reservation {} to rental", reservationId, ex);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
        }

        doGet(request, response);
    }

    private Integer parseInteger(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
