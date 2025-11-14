package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.config.ConfigManager;
import com.pinguela.rentexpres.exception.DataException;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.ReservationDTO;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.HeadquartersService;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.ReservationService;
import com.pinguela.rentexpres.service.impl.HeadquartersServiceImpl;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpres.service.impl.ReservationServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.ReservationConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/reservations")
public class PublicReservationServlet extends BasePrivateServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(PublicReservationServlet.class);
    private final ReservationService reservationService;
    private final HeadquartersService headquartersService;
    private final EmployeeService employeeService;
    private final Integer defaultEmployeeId;

    /*
     * Configura el servlet con las implementaciones estándar para gestionar
     * reservas, sedes y vehículos.
     */
    public PublicReservationServlet() {
        this(new ReservationServiceImpl(), new HeadquartersServiceImpl(), new EmployeeServiceImpl());
    }

    /*
     * Permite inyectar servicios alternativos (por ejemplo, durante pruebas) para
     * gestionar el flujo de reservas públicas.
     */
    PublicReservationServlet(ReservationService reservationService, HeadquartersService headquartersService,
            EmployeeService employeeService) {
        this.reservationService = reservationService;
        this.headquartersService = headquartersService;
        this.employeeService = employeeService;
        this.defaultEmployeeId = loadDefaultEmployeeId();
    }

    @Override
    /*
     * Carga el formulario de reserva, trasladando parámetros existentes y
     * preparando la información necesaria para su visualización.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configureEncoding(request, response);

        try {
            transferFlashAttributes(request);
            copyFormParameters(request);
            ensureEmployeeSelection(request);

            request.setAttribute(ReservationConstants.ATTR_HEADQUARTERS, headquartersService.findAll());
            VehicleDTO cartVehicle = exposeCartVehicle(request);
            preparePickupHeadquarters(request, cartVehicle);

            forward(request, response, Views.PUBLIC_RESERVATION_FORM);
        } catch (RentexpresException | DataException ex) {
            LOGGER.error("Unable to load reservation form data", ex);
            throw new ServletException("Unable to load reservation form data", ex);
        }
    }

    @Override
    /*
     * Procesa el envío del formulario de reserva validando la sesión, construyendo
     * la reserva y delegando en el servicio correspondiente.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configureEncoding(request, response);

        try {
            copyFormParameters(request);
            ensureEmployeeSelection(request);

            VehicleDTO cartVehicle = exposeCartVehicle(request);
            preparePickupHeadquarters(request, cartVehicle);
            request.setAttribute(ReservationConstants.ATTR_HEADQUARTERS, headquartersService.findAll());

            UserDTO user = SessionManager.getLoggedUser(request.getSession());
            if (user == null) {
                response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
                return;
            }

            ReservationDTO reservation = buildReservation(request, user, cartVehicle);
            boolean created = reservationService.create(reservation);
            if (created) {
                SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                        MessageResolver.getMessage(request, "reservation.form.flash.success"));
                response.sendRedirect(request.getContextPath() + "/public/reservations");
                return;
            }

            request.setAttribute(AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, "reservation.form.flash.error"));
            forward(request, response, Views.PUBLIC_RESERVATION_FORM);
        } catch (RentexpresException | DataException | DateTimeParseException | NumberFormatException ex) {
            LOGGER.error("Unable to process reservation request", ex);
            throw new ServletException("Unable to process reservation request", ex);
        }
    }

    /*
     * Determina la sede de recogida a partir del vehículo seleccionado, la
     * información detallada o los parámetros de la petición.
     */
    private void preparePickupHeadquarters(HttpServletRequest request, VehicleDTO vehicle)
            throws DataException, RentexpresException {
        Integer headquartersId = getIntegerParameter(request, ReservationConstants.PARAM_PICKUP_HEADQUARTERS_ID);

        if (headquartersId == null && vehicle != null) {
            headquartersId = vehicle.getCurrentHeadquartersId();
        }

        HeadquartersDTO pickup = headquartersId != null ? headquartersService.findById(headquartersId) : null;

        request.setAttribute(ReservationConstants.ATTR_PICKUP_HEADQUARTERS, pickup);
        request.setAttribute(ReservationConstants.ATTR_PARAM_PICKUP_HEADQUARTERS, headquartersId);
    }

    private void copyFormParameters(HttpServletRequest request) {
        request.setAttribute(ReservationConstants.ATTR_PARAM_START_DATE,
                request.getParameter(ReservationConstants.PARAM_START_DATE));
        request.setAttribute(ReservationConstants.ATTR_PARAM_START_TIME,
                request.getParameter(ReservationConstants.PARAM_START_TIME));
        request.setAttribute(ReservationConstants.ATTR_PARAM_END_DATE,
                request.getParameter(ReservationConstants.PARAM_END_DATE));
        request.setAttribute(ReservationConstants.ATTR_PARAM_END_TIME,
                request.getParameter(ReservationConstants.PARAM_END_TIME));
        request.setAttribute(ReservationConstants.ATTR_PARAM_RETURN_HEADQUARTERS,
                request.getParameter(ReservationConstants.PARAM_RETURN_HEADQUARTERS));
        request.setAttribute(ReservationConstants.ATTR_PARAM_EMPLOYEE_ID,
                request.getParameter(ReservationConstants.PARAM_EMPLOYEE_ID));
    }

    private ReservationDTO buildReservation(HttpServletRequest request, UserDTO user, VehicleDTO cartVehicle) {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setUserId(user.getUserId());
        reservation.setReservationStatusId(ReservationConstants.RESERVATION_STATUS_RESERVED_ID);

        if (cartVehicle != null) {
            reservation.setVehicleId(cartVehicle.getVehicleId());
        }

        Integer pickupHeadquarters = (Integer) request.getAttribute(ReservationConstants.ATTR_PARAM_PICKUP_HEADQUARTERS);
        if (pickupHeadquarters == null && cartVehicle != null) {
            pickupHeadquarters = cartVehicle.getCurrentHeadquartersId();
        }
        reservation.setPickupHeadquartersId(pickupHeadquarters);

        reservation.setReturnHeadquartersId(
                getIntegerParameter(request, ReservationConstants.PARAM_RETURN_HEADQUARTERS));

        Integer employeeId = resolveEmployeeId(request);
        if (employeeId != null) {
            reservation.setEmployeeId(employeeId);
        }

        reservation.setStartDate(buildDateTime(request, ReservationConstants.PARAM_START_DATE,
                ReservationConstants.PARAM_START_TIME));
        reservation.setEndDate(buildDateTime(request, ReservationConstants.PARAM_END_DATE,
                ReservationConstants.PARAM_END_TIME));

        return reservation;
    }

    private Integer resolveEmployeeId(HttpServletRequest request) {
        Integer fromRequest = getIntegerParameter(request, ReservationConstants.PARAM_EMPLOYEE_ID);
        if (fromRequest != null) {
            return fromRequest;
        }

        Integer sessionEmployeeId = resolveSessionEmployeeId(request);
        if (sessionEmployeeId != null) {
            return sessionEmployeeId;
        }

        return defaultEmployeeId;
    }

    private LocalDateTime buildDateTime(HttpServletRequest request, String dateParam, String timeParam) {
        String date = request.getParameter(dateParam);
        String time = request.getParameter(timeParam);
        if (date == null || date.isEmpty() || time == null || time.isEmpty()) {
            return null;
        }
        return LocalDate.parse(date).atTime(LocalTime.parse(time));
    }

    private void ensureEmployeeSelection(HttpServletRequest request) {
        Object currentValue = request.getAttribute(ReservationConstants.ATTR_PARAM_EMPLOYEE_ID);
        if (currentValue instanceof String && !((String) currentValue).isEmpty()) {
            return;
        }

        Integer employeeId = resolveSessionEmployeeId(request);
        if (employeeId == null) {
            employeeId = defaultEmployeeId;
        }

        if (employeeId != null) {
            request.setAttribute(ReservationConstants.ATTR_PARAM_EMPLOYEE_ID, String.valueOf(employeeId));
        }
    }

    private Integer resolveSessionEmployeeId(HttpServletRequest request) {
        Object sessionEmployee = SessionManager.get(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        if (sessionEmployee instanceof EmployeeDTO) {
            return ((EmployeeDTO) sessionEmployee).getId();
        }
        return null;
    }

    private Integer loadDefaultEmployeeId() {
        String configured = ConfigManager.getValue(ReservationConstants.CONFIG_DEFAULT_EMPLOYEE_ID);
        Integer defaultId = null;

        if (configured != null) {
            String trimmed = configured.trim();
            if (!trimmed.isEmpty()) {
                try {
                    Integer candidate = Integer.valueOf(trimmed);
                    if (candidate > 0) {
                        defaultId = validateEmployee(candidate);
                        if (defaultId == null) {
                            LOGGER.warn("Configured default employee id not found: {}", candidate);
                        }
                    } else {
                        LOGGER.warn("Invalid default employee id configured: {}", trimmed);
                    }
                } catch (NumberFormatException ex) {
                    LOGGER.warn("Invalid default employee id configured: {}", trimmed);
                }
            }
        }

        if (defaultId == null) {
            defaultId = resolveFirstEmployee();
        }

        return defaultId;
    }

    private Integer validateEmployee(Integer candidate) {
        if (employeeService == null) {
            return candidate;
        }
        try {
            EmployeeDTO employee = employeeService.findById(candidate);
            if (employee != null) {
                return candidate;
            }
        } catch (RentexpresException ex) {
            LOGGER.warn("Unable to validate default employee id {}", candidate, ex);
        }
        return null;
    }

    private Integer resolveFirstEmployee() {
        if (employeeService == null) {
            return null;
        }
        try {
            java.util.List<EmployeeDTO> employees = employeeService.findAll();
            if (employees != null && !employees.isEmpty()) {
                return employees.get(0).getId();
            }
        } catch (RentexpresException ex) {
            LOGGER.warn("Unable to resolve fallback employee id", ex);
        }
        return null;
    }

    private Integer getIntegerParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Integer.valueOf(value);
    }

    /*
     * Expone el vehículo almacenado en sesión (carrito) para que la vista pueda
     * mostrarlo y lo devuelve para su uso en el flujo de reserva.
     */
    private VehicleDTO exposeCartVehicle(HttpServletRequest request) {
        Object cartVehicle = SessionManager.getAttribute(request, ReservationConstants.ATTR_CART_VEHICLE);
        if (cartVehicle instanceof VehicleDTO) {
            request.setAttribute(ReservationConstants.ATTR_CART_VEHICLE, cartVehicle);
            return (VehicleDTO) cartVehicle;
        }
        return null;
    }

    @Override
    /*
     * Proporciona el logger asociado a este servlet para integrarse con la clase
     * base.
     */
    protected Logger getLogger() {
        return LOGGER;
    }
}
