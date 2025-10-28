package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.dao.HeadquartersDAO;
import com.pinguela.rentexpres.dao.impl.HeadquartersDAOImpl;
import com.pinguela.rentexpres.exception.DataException;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.VehicleCategoryDTO;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.VehicleCategoryService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.impl.VehicleCategoryServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpres.util.JDBCUtils;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.ReservationConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpressweb.security.SessionManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Gestiona la simulación de reservas en la zona privada.
 */
@WebServlet("/app/reservations/private")
public class PrivateReservationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter DATE_INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final DateTimeFormatter DATE_DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private static final Logger LOGGER = LogManager.getLogger(PrivateReservationServlet.class);

	private final VehicleService vehicleService = new VehicleServiceImpl();
	private final VehicleCategoryService categoryService = new VehicleCategoryServiceImpl();
	private final HeadquartersDAO headquartersDAO = new HeadquartersDAOImpl();

	public PrivateReservationServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Object summary = SessionManager.getAttribute(request, ReservationConstants.ATTR_RESERVATION_SUMMARY);
		Object reference = SessionManager.getAttribute(request, ReservationConstants.ATTR_RESERVATION_REFERENCE);
		String refParam = request.getParameter("ref");

		if (summary instanceof Map<?, ?> && reference != null && reference.equals(refParam)) {
			@SuppressWarnings("unchecked")
			Map<String, Object> reservationSummary = (Map<String, Object>) summary;
			SessionManager.removeAttribute(request, ReservationConstants.ATTR_RESERVATION_SUMMARY);
			SessionManager.removeAttribute(request, ReservationConstants.ATTR_RESERVATION_REFERENCE);

			request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Reserva simulada");
			request.setAttribute(ReservationConstants.ATTR_RESERVATION_SUMMARY, reservationSummary);
			request.setAttribute(ReservationConstants.ATTR_RESERVATION_REFERENCE, reference);
			request.getRequestDispatcher("/private/reservation/reservation_success.jsp").forward(request, response);
			return;
		}

		response.sendRedirect(request.getContextPath() + "/public/vehicles");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
		if (currentUser == null) {
			SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
					"Debes iniciar sesión para completar la simulación de la reserva.");
			response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
			return;
		}

		Map<String, String> formData = new HashMap<>();
		List<String> errors = new ArrayList<>();
		Locale locale = request.getLocale();

		String vehicleIdParam = request.getParameter(ReservationConstants.PARAM_VEHICLE_ID);
		formData.put(ReservationConstants.PARAM_VEHICLE_ID, vehicleIdParam);

		Integer vehicleId = parseInteger(vehicleIdParam);
		VehicleDTO vehicle = loadVehicle(vehicleId);

		if (vehicle == null) {
			SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
					"El vehículo indicado no está disponible. Selecciona un coche del catálogo.");
			response.sendRedirect(request.getContextPath() + "/public/vehicles?notfound=1");
			return;
		}

		LocalDate startDate = parseDate(request.getParameter(ReservationConstants.PARAM_START_DATE),
				"La fecha de recogida es obligatoria y debe tener formato válido.", errors);
		LocalDate endDate = parseDate(request.getParameter(ReservationConstants.PARAM_END_DATE),
				"La fecha de devolución es obligatoria y debe tener formato válido.", errors);
		formData.put(ReservationConstants.PARAM_START_DATE,
				request.getParameter(ReservationConstants.PARAM_START_DATE));
		formData.put(ReservationConstants.PARAM_END_DATE, request.getParameter(ReservationConstants.PARAM_END_DATE));

		if (startDate != null && endDate != null && !endDate.isAfter(startDate)) {
			errors.add("La devolución debe ser posterior a la recogida.");
		}

		List<HeadquartersDTO> headquarters = loadHeadquarters();
		Map<Integer, HeadquartersDTO> headquartersById = headquarters.stream()
				.filter(hq -> hq.getHeadquartersId() != null)
				.collect(Collectors.toMap(HeadquartersDTO::getHeadquartersId, hq -> hq));

		String pickupParam = request.getParameter(ReservationConstants.PARAM_PICKUP_HEADQUARTERS);
		String dropoffParam = request.getParameter(ReservationConstants.PARAM_RETURN_HEADQUARTERS);
		formData.put(ReservationConstants.PARAM_PICKUP_HEADQUARTERS, pickupParam);
		formData.put(ReservationConstants.PARAM_RETURN_HEADQUARTERS, dropoffParam);

		Integer pickupId = parseInteger(pickupParam);
		Integer dropoffId = parseInteger(dropoffParam);

		HeadquartersDTO pickup = pickupId == null ? null : headquartersById.get(pickupId);
		HeadquartersDTO dropoff = dropoffId == null ? null : headquartersById.get(dropoffId);

		if (pickup == null) {
			errors.add("Selecciona una sede de recogida válida.");
		}
		if (dropoff == null) {
			errors.add("Selecciona una sede de devolución válida.");
		}

		if (!errors.isEmpty()) {
			request.setAttribute(ReservationConstants.ATTR_RESERVATION_ERRORS, errors);
			request.setAttribute(ReservationConstants.ATTR_RESERVATION_FORM, formData);
			request.setAttribute(ReservationConstants.ATTR_HEADQUARTERS, headquarters);
			request.setAttribute(VehicleConstants.ATTR_SELECTED_VEHICLE, vehicle);
			request.setAttribute(VehicleConstants.ATTR_SELECTED_CATEGORY_NAME,
					resolveCategoryName(vehicle.getCategoryId(), locale));
			request.setAttribute(VehicleConstants.ATTR_RELATED_VEHICLES, findRelatedVehicles(vehicle));
			request.getRequestDispatcher("/public/vehicle/vehicle_detail.jsp").forward(request, response);
			return;
		}

		int rentalDays = (int) ChronoUnit.DAYS.between(startDate, endDate);
		BigDecimal dailyPrice = vehicle.getDailyPrice() == null ? BigDecimal.ZERO : vehicle.getDailyPrice();
		BigDecimal vehicleSubtotal = dailyPrice.multiply(BigDecimal.valueOf(rentalDays));
		BigDecimal total = vehicleSubtotal;

		String reference = generateReference();
		Map<String, Object> summary = new HashMap<>();
		summary.put("vehicle", vehicle);
		summary.put("vehicleCategoryName", resolveCategoryName(vehicle.getCategoryId(), locale));
		summary.put("startDate", startDate);
		summary.put("endDate", endDate);
		summary.put("formattedStartDate", formatDisplayDate(startDate));
		summary.put("formattedEndDate", formatDisplayDate(endDate));
		summary.put("rentalDays", rentalDays);
		summary.put("vehicleSubtotal", vehicleSubtotal);
		summary.put("total", total);
		summary.put("pickupHeadquarters", pickup != null ? pickup.getName() : null);
		summary.put("returnHeadquarters", dropoff != null ? dropoff.getName() : null);
		summary.put("contactEmail", currentUser.toString());
		summary.put("reference", reference);

		SessionManager.setAttribute(request, ReservationConstants.ATTR_RESERVATION_SUMMARY, summary);
		SessionManager.setAttribute(request, ReservationConstants.ATTR_RESERVATION_REFERENCE, reference);

		response.sendRedirect(request.getContextPath() + "/app/reservations/private?ref=" + reference);
	}

	private Integer parseInteger(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private VehicleDTO loadVehicle(Integer vehicleId) {
		if (vehicleId == null) {
			return null;
		}
		try {
			return vehicleService.findById(vehicleId);
		} catch (RentexpresException ex) {
			LOGGER.error("No se pudo cargar el vehículo {}", vehicleId, ex);
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
			LOGGER.warn("No se pudo recuperar la categoría {}", categoryId, ex);
			return null;
		}
	}

	private List<VehicleDTO> findRelatedVehicles(VehicleDTO vehicle) {
		try {
			return vehicleService.findAll().stream()
					.filter(other -> other.getVehicleId() != null
							&& !other.getVehicleId().equals(vehicle.getVehicleId()))
					.filter(other -> vehicle.getCategoryId() != null
							&& vehicle.getCategoryId().equals(other.getCategoryId()))
					.limit(3).collect(Collectors.toList());
		} catch (RentexpresException ex) {
			LOGGER.warn("No se pudieron cargar vehículos relacionados", ex);
			return new ArrayList<>();
		}
	}

	private List<HeadquartersDTO> loadHeadquarters() {
		Connection connection = null;
		try {
			connection = JDBCUtils.getConnection();
			JDBCUtils.beginTransaction(connection);
			List<HeadquartersDTO> list = headquartersDAO.findAll(connection);
			JDBCUtils.commitTransaction(connection);
			return list;
		} catch (SQLException | DataException ex) {
			JDBCUtils.rollbackTransaction(connection);
			LOGGER.error("Error al obtener el listado de sedes", ex);
			return new ArrayList<>();
		} finally {
			JDBCUtils.close(connection);
		}
	}

	private LocalDate parseDate(String rawValue, String errorMessage, List<String> errors) {
		if (rawValue == null || rawValue.trim().isEmpty()) {
			errors.add(errorMessage);
			return null;
		}
		try {
			return LocalDate.parse(rawValue.trim(), DATE_INPUT_FORMAT);
		} catch (DateTimeParseException ex) {
			errors.add(errorMessage);
			return null;
		}
	}

	private String generateReference() {
		return "RS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	private String formatDisplayDate(LocalDate date) {
		return date != null ? date.format(DATE_DISPLAY_FORMAT) : "";
	}
}
