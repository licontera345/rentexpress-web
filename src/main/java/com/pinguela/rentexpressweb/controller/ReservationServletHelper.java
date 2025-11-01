package com.pinguela.rentexpressweb.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.model.VehicleStatusDTO;
import com.pinguela.rentexpres.service.VehicleStatusService;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.util.SessionManager;

import jakarta.servlet.http.HttpServletRequest;

final class ReservationServletHelper {
	private ReservationServletHelper() {
	}

	static String normalize(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	static LocalDate parseDate(String value) {
		if (value == null) {
			return null;
		}
		try {
			return LocalDate.parse(value);
		} catch (Exception ex) {
			return null;
		}
	}

	static LocalDateTime toDateTime(LocalDate date) {
		return date != null ? date.atStartOfDay() : null;
	}

	static Integer parseInteger(String value) {
		if (value == null) {
			return null;
		}
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	static boolean isVehicleAvailable(HttpServletRequest request, VehicleDTO vehicle,
	        VehicleStatusService vehicleStatusService) throws RentexpresException {
	    if (vehicle == null) {
	        return false;
	    }

	    // Comprobamos si el estado del vehículo coincide con “Disponible”
	    Integer availableStatusId = resolveVehicleStatusId(request, vehicleStatusService, "DISPONIBLE", "AVAILABLE");
	    Integer statusId = vehicle.getVehicleStatusId();

	    // Si el ID coincide con el de “Disponible”
	    if (availableStatusId != null && availableStatusId.equals(statusId)) {
	        return true;
	    }

	    // Compatibilidad por si statusId==1 representa “Disponible”
	    if (statusId != null && statusId.intValue() == 1) {
	        return true;
	    }

	    // Comprobación por nombre de estado (a través del DTO)
	    VehicleStatusDTO status = (VehicleStatusDTO) vehicle.getVehicleStatus();
	    if (status != null && status.getStatusName() != null) {
	        String normalized = status.getStatusName().trim().toLowerCase(Locale.ROOT);
	        if (normalized.equals("disponible") || normalized.equals("available")) {
	            return true;
	        }
	    }

	    return false;
	}

	static Integer resolveVehicleStatusId(HttpServletRequest request, VehicleStatusService vehicleStatusService,
			String... desiredNames) throws RentexpresException {
		String language = resolveLanguage(request);
		List<VehicleStatusDTO> statuses = vehicleStatusService.findAll(language);
		if (statuses == null || statuses.isEmpty()) {
			return null;
		}
		boolean reservedRequested = false;
		if (desiredNames != null) {
			for (String desired : desiredNames) {
				if (desired != null && desired.trim().equalsIgnoreCase("RESERVADO")) {
					reservedRequested = true;
					break;
				}
			}
		}
		VehicleStatusDTO reservedStatus = null;
		for (VehicleStatusDTO status : statuses) {
			if (status == null || status.getStatusName() == null) {
				continue;
			}
			String statusName = status.getStatusName().trim();
			if (statusName.isEmpty()) {
				continue;
			}
			if (statusName.equalsIgnoreCase("RESERVADO")) {
				reservedStatus = status;
				if (reservedRequested) {
					return reservedStatus.getVehicleStatusId();
				}
			}
			String normalized = statusName.toLowerCase(Locale.ROOT);
			if (desiredNames != null) {
				for (String desired : desiredNames) {
					if (desired != null && normalized.equals(desired.toLowerCase(Locale.ROOT))) {
						return status.getVehicleStatusId();
					}
				}
			}
		}
		if (reservedRequested && reservedStatus != null) {
			return reservedStatus.getVehicleStatusId();
		}
		return null;
	}

	private static String resolveLanguage(HttpServletRequest request) {
		Object storedLocale = SessionManager.get(request, AppConstants.ATTR_LOCALE);
		if (storedLocale instanceof String) {
			String languageTag = ((String) storedLocale).trim();
			if (!languageTag.isEmpty()) {
				Locale locale = Locale.forLanguageTag(languageTag);
				if (!locale.getLanguage().isEmpty()) {
					return locale.getLanguage();
				}
				return languageTag;
			}
		} else if (storedLocale instanceof Locale) {
			Locale locale = (Locale) storedLocale;
			if (!locale.getLanguage().isEmpty()) {
				return locale.getLanguage();
			}
		}
		return "es";
	}
}
