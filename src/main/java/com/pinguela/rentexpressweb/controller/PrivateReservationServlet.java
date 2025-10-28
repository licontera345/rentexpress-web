package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.ReservationDTO;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.ReservationService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.impl.ReservationServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpressweb.constants.ApplicationConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * PrivateReservationServlet
 * Permite crear reservas de vehículos por parte de clientes logueados.
 */
@WebServlet(name = "PrivateReservationServlet", urlPatterns = { ApplicationConstants.ServletPath.PRIVATE_RESERVATION_PATH })
public class PrivateReservationServlet extends HttpServlet {

    private static final long serialVersionUID = ApplicationConstants.Serialization.DEFAULT_SERIAL_VERSION_UID;
    private static final Logger logger = LogManager.getLogger(PrivateReservationServlet.class);

    private final ReservationService reservationService = new ReservationServiceImpl();
    private final VehicleService vehicleService = new VehicleServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //solo client
        String role = (String) req.getSession().getAttribute(ApplicationConstants.SessionAttribute.ROLE);
        if (role == null || !role.equals(ApplicationConstants.Role.ROLE_CLIENT)) {
            resp.sendRedirect(req.getContextPath() + ApplicationConstants.Auth.LOGIN_PATH);
            return;
        }

        String vehicleIdParam = req.getParameter(ApplicationConstants.RequestParam.VEHICLE_ID);
        if (vehicleIdParam == null) {
            resp.sendRedirect(req.getContextPath() + ApplicationConstants.ServletPath.PUBLIC_VEHICLES_PATH);
            return;
        }

        try {
            Integer vehicleId = Integer.parseInt(vehicleIdParam);
            VehicleDTO vehicle = vehicleService.findById(vehicleId);
            if (vehicle == null) {
                req.setAttribute(ApplicationConstants.RequestAttribute.ERROR_MESSAGE,
                        ApplicationConstants.Message.VEHICLE_NOT_FOUND);
            } else {
                req.setAttribute(ApplicationConstants.RequestAttribute.VEHICLE, vehicle);
            }

            req.getRequestDispatcher(ApplicationConstants.ViewPath.PRIVATE_RESERVATION_FORM_VIEW).forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + ApplicationConstants.ServletPath.PUBLIC_VEHICLES_PATH);
        } catch (RentexpresException e) {
            logger.error(ApplicationConstants.LogMessage.RESERVATION_VEHICLE_ERROR, e);
            req.setAttribute(ApplicationConstants.RequestAttribute.ERROR_MESSAGE,
                    ApplicationConstants.Message.VEHICLE_ERROR_GENERIC);
            req.getRequestDispatcher(ApplicationConstants.ViewPath.PRIVATE_RESERVATION_FORM_VIEW).forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String role = (String) req.getSession().getAttribute(ApplicationConstants.SessionAttribute.ROLE);
        if (role == null || !role.equals(ApplicationConstants.Role.ROLE_CLIENT)) {
            resp.sendRedirect(req.getContextPath() + ApplicationConstants.Auth.LOGIN_PATH);
            return;
        }

        try {
            UserDTO user = (UserDTO) req.getSession().getAttribute(ApplicationConstants.SessionAttribute.ACCOUNT);
            Integer vehicleId = Integer.parseInt(req.getParameter(ApplicationConstants.RequestParam.VEHICLE_ID));
            String startDateStr = req.getParameter(ApplicationConstants.RequestParam.START_DATE);
            String endDateStr = req.getParameter(ApplicationConstants.RequestParam.END_DATE);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ApplicationConstants.Format.ISO_DATE_FORMAT);
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            if (endDate.isBefore(startDate)) {
                req.setAttribute(ApplicationConstants.RequestAttribute.ERROR_MESSAGE,
                        ApplicationConstants.Message.END_DATE_BEFORE_START);
                req.getRequestDispatcher(ApplicationConstants.ViewPath.PRIVATE_RESERVATION_FORM_VIEW).forward(req, resp);
                return;
            }

            // Verificar disponibilidad (según tu lógica DAO)
//            boolean available = reservationService.isVehicleAvailable(vehicleId, startDate, endDate);
//            if (!available) {
//                req.setAttribute("errorMsg", "El vehículo no está disponible en esas fechas.");
//                req.getRequestDispatcher("/WEB-INF/views/private/reservation-form.jsp").forward(req, resp);
//                return;
//            }

            // Crear objeto de reserva
            ReservationDTO reservation = new ReservationDTO();
            reservation.setUserId(user.getUserId());
            reservation.setVehicleId(vehicleId);
            reservation.setStartDate(startDate.atStartOfDay());
            reservation.setEndDate(endDate.atStartOfDay());
            reservation.setReservationStatusId(ApplicationConstants.Number.RESERVATION_STATUS_RESERVED_ID); // "Reservado" (ajustar ID real)
//            reservation.setTotalPrice(reservationService.calculateTotalPrice(vehicleId, startDate, endDate));

            reservationService.create(reservation);

            logger.info(ApplicationConstants.LogMessage.RESERVATION_CREATED, user.getUsername(), vehicleId);
            resp.sendRedirect(req.getContextPath() + ApplicationConstants.Reservation.RESERVATION_SUCCESS_PATH
                    + reservation.getReservationId());

        } catch (Exception e) {
            logger.error(ApplicationConstants.LogMessage.RESERVATION_CREATION_ERROR, e);
            req.setAttribute(ApplicationConstants.RequestAttribute.ERROR_MESSAGE,
                    ApplicationConstants.Message.RESERVATION_CREATION_ERROR_PREFIX + e.getMessage());
            req.getRequestDispatcher(ApplicationConstants.ViewPath.PRIVATE_RESERVATION_FORM_VIEW).forward(req, resp);
        }
    }
}