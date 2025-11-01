package com.pinguela.rentexpressweb.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.model.VehicleStatusDTO;
import com.pinguela.rentexpres.service.VehicleStatusService;

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
        Integer availableStatusId = resolveVehicleStatusId(request, vehicleStatusService, "Disponible", "Available");
        Integer statusId = vehicle.getVehicleStatusId();
        if (availableStatusId != null && availableStatusId.equals(statusId)) {
            return true;
        }
        if (statusId != null && statusId.intValue() == 1) {
            return true;
        }
        VehicleStatusDTO status = vehicle.getVehicleStatus();
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
        List<VehicleStatusDTO> statuses = vehicleStatusService.findAll();
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        for (VehicleStatusDTO status : statuses) {
            if (status == null || status.getStatusName() == null) {
                continue;
            }
            String statusName = status.getStatusName().trim();
            if (statusName.equalsIgnoreCase("RESERVADO")) {
                return status.getVehicleStatusId();
            }
            String normalized = statusName.toLowerCase(Locale.ROOT);
            for (String desired : desiredNames) {
                if (desired != null && normalized.equals(desired.toLowerCase(Locale.ROOT))) {
                    return status.getVehicleStatusId();
                }
            }
        }
        return null;
    }
}
