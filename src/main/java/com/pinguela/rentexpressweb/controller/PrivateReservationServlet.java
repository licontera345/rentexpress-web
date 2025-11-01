package com.pinguela.rentexpressweb.controller;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.ReservationDTO;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.ReservationService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.VehicleStatusService;
import com.pinguela.rentexpres.service.impl.ReservationServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleStatusServiceImpl;
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
    private static final String KEY_ERROR_RESERVATION_CREATE = "error.reservation.createFailed";
    private static final Logger LOGGER = LogManager.getLogger(PrivateReservationServlet.class);
    private transient VehicleService vehicleService;
    private transient ReservationService reservationService;
    private transient VehicleStatusService vehicleStatusService;
    @Override
    public void init() throws ServletException {
        super.init();
        this.vehicleService = new VehicleServiceImpl();
        this.reservationService = new ReservationServiceImpl();
        this.vehicleStatusService = new VehicleStatusServiceImpl();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        UserDTO currentUser = (UserDTO) SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        Integer vehicleId = ReservationServletHelper
                .parseInteger(request.getParameter(ReservationConstants.PARAM_VEHICLE_ID));
        if (vehicleId != null) {
            try {
                VehicleDTO vehicle = vehicleService.findById(vehicleId);
                request.setAttribute(ReservationConstants.ATTR_PARAM_VEHICLE_ID, vehicle);
            } catch (RentexpresException ex) {
                LOGGER.error("Unable to load vehicle {} for reservation form", vehicleId, ex);
                request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
            }
        }
        request.getRequestDispatcher(Views.PUBLIC_VEHICLE_DETAIL).forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        UserDTO currentUser = (UserDTO) SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        Map<String, String> errors = new LinkedHashMap<String, String>();
        Map<String, String> form = new LinkedHashMap<String, String>();
        String vehicleId = ReservationServletHelper
                .normalize(request.getParameter(ReservationConstants.PARAM_VEHICLE_ID));
        String startDate = ReservationServletHelper
                .normalize(request.getParameter(ReservationConstants.PARAM_START_DATE));
        String endDate = ReservationServletHelper
                .normalize(request.getParameter(ReservationConstants.PARAM_END_DATE));
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
        LocalDate start = ReservationServletHelper.parseDate(startDate);
        LocalDate end = ReservationServletHelper.parseDate(endDate);
        if (start != null && end != null && start.isAfter(end)) {
            errors.put(ReservationConstants.PARAM_END_DATE,
                    MessageResolver.getMessage(request, "error.validation.invalidDateRange"));
        }
        Integer vehicleIdentifier = vehicleId != null ? ReservationServletHelper.parseInteger(vehicleId) : null;
        VehicleDTO vehicle = null;
        if (vehicleIdentifier != null && errors.isEmpty()) {
            try {
                vehicle = vehicleService.findById(vehicleIdentifier);
                if (vehicle == null) {
                    errors.put(ReservationConstants.PARAM_VEHICLE_ID,
                            MessageResolver.getMessage(request, "error.validation.vehicleNotFound"));
                } else if (!ReservationServletHelper.isVehicleAvailable(request, vehicle, vehicleStatusService)) {
                    errors.put(ReservationConstants.PARAM_VEHICLE_ID,
                            MessageResolver.getMessage(request, "error.validation.vehicleUnavailable"));
                }
            } catch (RentexpresException ex) {
                LOGGER.error("Error validating vehicle {} for reservation", vehicleIdentifier, ex);
                errors.put(ReservationConstants.PARAM_VEHICLE_ID, ex.getMessage());
            }
        }
        if (!errors.isEmpty()) {
            request.setAttribute(ReservationConstants.ATTR_RESERVATION_ERRORS, errors);
            request.setAttribute(ReservationConstants.ATTR_RESERVATION_FORM, form);
            request.getRequestDispatcher(Views.PUBLIC_VEHICLE_DETAIL).forward(request, response);
            return;
        }
        try {
            ReservationDTO reservation = new ReservationDTO();
            reservation.setVehicleId(vehicleIdentifier);
            reservation.setUserId(currentUser.getUserId());
            reservation.setStartDate(ReservationServletHelper.toDateTime(start));
            reservation.setEndDate(ReservationServletHelper.toDateTime(end));
            reservation.setReservationStatusId(Integer.valueOf(1));
            Integer reservedStatusId = ReservationServletHelper.resolveVehicleStatusId(request, vehicleStatusService,
                    "Reservado", "Reserved");
            if (reservedStatusId == null) {
                errors.put(ReservationConstants.PARAM_VEHICLE_ID,
                        MessageResolver.getMessage(request, "error.validation.vehicleUnavailable"));
                request.setAttribute(ReservationConstants.ATTR_RESERVATION_ERRORS, errors);
                request.setAttribute(ReservationConstants.ATTR_RESERVATION_FORM, form);
                request.getRequestDispatcher(Views.PUBLIC_VEHICLE_DETAIL).forward(request, response);
                return;
            }
            Integer previousStatusId = vehicle != null ? vehicle.getVehicleStatusId() : null;
            boolean statusChanged = false;
            try {
                if (vehicle != null) {
                    vehicle.setVehicleStatusId(reservedStatusId);
                    vehicleService.update(vehicle);
                    statusChanged = true;
                }
                boolean created = reservationService.create(reservation);
                if (!created) {
                    throw new RentexpresException(MessageResolver.getMessage(request, KEY_ERROR_RESERVATION_CREATE));
                }
                SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                        MessageResolver.getMessage(request, KEY_FLASH_SUCCESS));
                response.sendRedirect(request.getContextPath() + Views.PRIVATE_RESERVATION_SUCCESS);
            } catch (RentexpresException ex) {
                if (statusChanged && vehicle != null) {
                    try {
                        vehicle.setVehicleStatusId(previousStatusId);
                        vehicleService.update(vehicle);
                    } catch (RentexpresException rollbackEx) {
                        LOGGER.error("Failed to rollback vehicle status for vehicle {}", vehicleIdentifier, rollbackEx);
                    }
                }
                LOGGER.error("Error creating reservation", ex);
                errors.put(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
                request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
                request.setAttribute(ReservationConstants.ATTR_RESERVATION_ERRORS, errors);
                request.setAttribute(ReservationConstants.ATTR_RESERVATION_FORM, form);
                request.getRequestDispatcher(Views.PUBLIC_VEHICLE_DETAIL).forward(request, response);
            }
        } catch (RentexpresException ex) {
            LOGGER.error("Error creating reservation", ex);
            errors.put(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
            request.setAttribute(ReservationConstants.ATTR_RESERVATION_ERRORS, errors);
            request.setAttribute(ReservationConstants.ATTR_RESERVATION_FORM, form);
            request.getRequestDispatcher(Views.PUBLIC_VEHICLE_DETAIL).forward(request, response);
        }
    }
}
