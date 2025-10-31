package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.VehicleCategoryDTO;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.HeadquartersService;
import com.pinguela.rentexpres.service.VehicleCategoryService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.impl.HeadquartersServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleCategoryServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.ReservationConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.util.LegacyDateUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Gestiona la simulación de reservas en la zona privada.
 */
@WebServlet("/app/reservations/private")
public class PrivateReservationServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	private static final String DISPLAY_DATE_PATTERN = "dd/MM/yyyy";

	private static final Logger LOGGER = LogManager.getLogger(PrivateReservationServlet.class);

	private final VehicleService vehicleService = new VehicleServiceImpl();
	private final VehicleCategoryService categoryService = new VehicleCategoryServiceImpl();
	private final HeadquartersService headquartersService = new HeadquartersServiceImpl();

	public PrivateReservationServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!requireUser(request, response, "Debes iniciar sesión para revisar la reserva simulada.")) {
			return;
		}

		disableCaching(response);

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
			forward(request, response, "/private/reservation/reservation_success.jsp");
			return;
		}

		redirect(request, response, "/public/vehicles");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!requireUser(request, response, "Debes iniciar sesión para completar la simulación de la reserva.")) {
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
			redirect(request, response, "/public/vehicles?notfound=1");
			return;
		}

		Date startDate = parseDate(request.getParameter(ReservationConstants.PARAM_START_DATE),
				"La fecha de recogida es obligatoria y debe tener formato válido.", errors);
		Date endDate = parseDate(request.getParameter(ReservationConstants.PARAM_END_DATE),
				"La fecha de devolución es obligatoria y debe tener formato válido.", errors);
		formData.put(ReservationConstants.PARAM_START_DATE,
				request.getParameter(ReservationConstants.PARAM_START_DATE));
		formData.put(ReservationConstants.PARAM_END_DATE, request.getParameter(ReservationConstants.PARAM_END_DATE));

		if (startDate != null && endDate != null && !endDate.after(startDate)) {
			errors.add("La devolución debe ser posterior a la recogida.");
		}

		List<HeadquartersDTO> headquarters = loadHeadquarters();
		Map<Integer, HeadquartersDTO> headquartersById = new HashMap<>();
		for (HeadquartersDTO headquartersDTO : headquarters) {
			if (headquartersDTO != null && headquartersDTO.getId() != null) {
				headquartersById.put(headquartersDTO.getId(), headquartersDTO);
			}
		}

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
			exposeReservationParameterNames(request);
			forward(request, response, "/public/vehicle/vehicle_detail.jsp");
			return;
		}

		int rentalDays = LegacyDateUtils.daysBetween(startDate, endDate);
		BigDecimal dailyPrice = vehicle.getDailyPrice() == null ? BigDecimal.ZERO : vehicle.getDailyPrice();
		BigDecimal vehicleSubtotal = dailyPrice.multiply(BigDecimal.valueOf(rentalDays));
		BigDecimal total = vehicleSubtotal;

		Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
		String reference = generateReference();
		Map<String, Object> summary = new HashMap<>();
		summary.put("vehicle", vehicle);
		summary.put("vehicleCategoryName", resolveCategoryName(vehicle.getCategoryId(), locale));
		summary.put("startDate", startDate);
		summary.put("endDate", endDate);
		summary.put("formattedStartDate", formatDisplayDate(startDate));
		summary.put("formattedEndDate", formatDisplayDate(endDate));
		summary.put("rentalDays", Integer.valueOf(rentalDays));
		summary.put("vehicleSubtotal", vehicleSubtotal);
		summary.put("total", total);
		summary.put("pickupHeadquarters", pickup != null ? pickup.getName() : null);
		summary.put("returnHeadquarters", dropoff != null ? dropoff.getName() : null);
		summary.put("contactEmail", currentUser != null ? currentUser.toString() : null);
		summary.put("reference", reference);

		SessionManager.setAttribute(request, ReservationConstants.ATTR_RESERVATION_SUMMARY, summary);
		SessionManager.setAttribute(request, ReservationConstants.ATTR_RESERVATION_REFERENCE, reference);

		redirect(request, response, "/app/reservations/private?ref=" + reference);
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

	private Date parseDate(String rawValue, String errorMessage, List<String> errors) {
		if (rawValue == null || rawValue.trim().isEmpty()) {
			errors.add(errorMessage);
			return null;
		}
		Date parsed = LegacyDateUtils.parseIsoDate(rawValue);
		if (parsed == null) {
			errors.add(errorMessage);
		}
		return parsed;
	}

	private VehicleDTO loadVehicle(Integer vehicleId) {
		if (vehicleId == null) {
			return null;
		}
		try {
			return vehicleService.findById(vehicleId);
		} catch (Exception ex) {
			LOGGER.error("No se pudo cargar el vehículo {}", vehicleId, ex);
			return null;
		}
	}

	private List<HeadquartersDTO> loadHeadquarters() {
		try {
			List<HeadquartersDTO> headquarters = headquartersService.findAll();
			if (headquarters == null) {
				LOGGER.error("Error al obtener el listado de sedes");
				return new ArrayList<HeadquartersDTO>();
			}
			return headquarters;
		} catch (Exception ex) {
			LOGGER.error("Error al obtener el listado de sedes", ex);
			return new ArrayList<HeadquartersDTO>();
		}
	}

	private List<VehicleDTO> findRelatedVehicles(VehicleDTO vehicle) {
		try {
			List<VehicleDTO> related = new ArrayList<>();
			List<VehicleDTO> allVehicles = vehicleService.findAll();
			for (VehicleDTO other : allVehicles) {
				if (other == null || other.getVehicleId() == null
						|| other.getVehicleId().equals(vehicle.getVehicleId())) {
					continue;
				}
				if (vehicle.getCategoryId() != null && vehicle.getCategoryId().equals(other.getCategoryId())) {
					related.add(other);
					if (related.size() == 3) {
						break;
					}
				}
			}
			return related;
		} catch (Exception ex) {
			LOGGER.warn("No se pudieron cargar vehículos relacionados", ex);
			return new ArrayList<>();
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
		} catch (Exception ex) {
			LOGGER.warn("No se pudo recuperar la categoría {}", categoryId, ex);
			return null;
		}
	}

	private void exposeReservationParameterNames(HttpServletRequest request) {
		request.setAttribute(ReservationConstants.ATTR_PARAM_VEHICLE_ID, ReservationConstants.PARAM_VEHICLE_ID);
		request.setAttribute(ReservationConstants.ATTR_PARAM_START_DATE, ReservationConstants.PARAM_START_DATE);
		request.setAttribute(ReservationConstants.ATTR_PARAM_END_DATE, ReservationConstants.PARAM_END_DATE);
		request.setAttribute(ReservationConstants.ATTR_PARAM_PICKUP_HEADQUARTERS,
				ReservationConstants.PARAM_PICKUP_HEADQUARTERS);
		request.setAttribute(ReservationConstants.ATTR_PARAM_RETURN_HEADQUARTERS,
				ReservationConstants.PARAM_RETURN_HEADQUARTERS);
	}

	private String generateReference() {
		return "RS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	private String formatDisplayDate(Date date) {
		return LegacyDateUtils.formatDate(date, DISPLAY_DATE_PATTERN);
	}
}
