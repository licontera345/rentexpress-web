package com.pinguela.rentexpressweb.service.employee;

import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import com.pinguela.rentexpressweb.util.ControllerUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Factoría encargada de traducir los parámetros HTTP a {@link EmployeeDirectoryFilter}.
 */
public class EmployeeDirectoryFilterFactory {

    public EmployeeDirectoryFilter build(HttpServletRequest request, Map<String, String> errors) {
        EmployeeDirectoryFilter.Builder builder = EmployeeDirectoryFilter.builder();

        String search = ControllerUtils.trimToNull(request.getParameter(EmployeeConstants.PARAM_SEARCH));
        builder.search(search);

        String headquartersRaw = ControllerUtils.trimToNull(
                request.getParameter(EmployeeConstants.PARAM_HEADQUARTERS));
        Integer headquartersId = parseInteger(headquartersRaw, errors);
        builder.headquartersId(headquartersId, headquartersRaw);

        String activeRaw = request.getParameter(EmployeeConstants.PARAM_ACTIVE);
        String normalizedActive = normalizeActive(activeRaw);
        Boolean active = parseActive(normalizedActive, errors);
        builder.active(active, normalizedActive);

        return builder.build();
    }

    private Integer parseInteger(String value, Map<String, String> errors) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            if (errors != null) {
                errors.put(EmployeeConstants.PARAM_HEADQUARTERS, "Selecciona una sede válida.");
            }
            return null;
        }
    }

    private String normalizeActive(String raw) {
        String normalized = ControllerUtils.normalizeLowerCase(raw);
        if (normalized == null || normalized.isEmpty()) {
            return EmployeeConstants.VALUE_ALL;
        }
        return normalized;
    }

    private Boolean parseActive(String value, Map<String, String> errors) {
        if (value == null || EmployeeConstants.VALUE_ALL.equalsIgnoreCase(value)) {
            return null;
        }
        if (EmployeeConstants.VALUE_ACTIVE.equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        }
        if (EmployeeConstants.VALUE_INACTIVE.equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        }
        if (errors != null) {
            errors.put(EmployeeConstants.PARAM_ACTIVE, "El estado indicado no es válido.");
        }
        return null;
    }
}
