package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.ReservationConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/reservations/create")
public class PrivateReservationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String KEY_FLASH_SUCCESS = "reservation.flash.success";
    private static final String KEY_ERROR_VEHICLE_REQUIRED = "error.validation.vehicleRequired";
    private static final String KEY_ERROR_START_REQUIRED = "error.validation.startDateRequired";
    private static final String KEY_ERROR_END_REQUIRED = "error.validation.endDateRequired";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (SessionManager.get(request, AppConstants.ATTR_CURRENT_USER) == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        request.getRequestDispatcher(Views.PUBLIC_VEHICLE_DETAIL).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (SessionManager.get(request, AppConstants.ATTR_CURRENT_USER) == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        Map<String, String> errors = new LinkedHashMap<String, String>();
        Map<String, String> form = new LinkedHashMap<String, String>();

        String vehicleId = normalize(request.getParameter(ReservationConstants.PARAM_VEHICLE_ID));
        String startDate = normalize(request.getParameter(ReservationConstants.PARAM_START_DATE));
        String endDate = normalize(request.getParameter(ReservationConstants.PARAM_END_DATE));

        if (vehicleId == null) {
            errors.put(ReservationConstants.PARAM_VEHICLE_ID,
                    MessageResolver.getMessage(request, KEY_ERROR_VEHICLE_REQUIRED));
        } else {
            form.put(ReservationConstants.PARAM_VEHICLE_ID, vehicleId);
        }
        if (startDate == null) {
            errors.put(ReservationConstants.PARAM_START_DATE,
                    MessageResolver.getMessage(request, KEY_ERROR_START_REQUIRED));
        } else {
            form.put(ReservationConstants.PARAM_START_DATE, startDate);
        }
        if (endDate == null) {
            errors.put(ReservationConstants.PARAM_END_DATE,
                    MessageResolver.getMessage(request, KEY_ERROR_END_REQUIRED));
        } else {
            form.put(ReservationConstants.PARAM_END_DATE, endDate);
        }

        if (!errors.isEmpty()) {
            request.setAttribute(ReservationConstants.ATTR_RESERVATION_ERRORS, errors);
            request.setAttribute(ReservationConstants.ATTR_RESERVATION_FORM, form);
            request.getRequestDispatcher(Views.PUBLIC_VEHICLE_DETAIL).forward(request, response);
            return;
        }

        SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                MessageResolver.getMessage(request, KEY_FLASH_SUCCESS));
        response.sendRedirect(request.getContextPath() + Views.PRIVATE_RESERVATION_SUCCESS);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
