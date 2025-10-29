package com.pinguela.rentexpressweb.security;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utilidad para sincronizar en sesión la información del empleado autenticado.
 */
public final class EmployeeSessionResolver {

    private static final Logger LOGGER = LogManager.getLogger(EmployeeSessionResolver.class);
    private static final EmployeeService EMPLOYEE_SERVICE = new EmployeeServiceImpl();

    private EmployeeSessionResolver() {
        // Utility class
    }

    public static void resolveFromEmail(HttpServletRequest request, String email) {
        if (request == null) {
            return;
        }
        SessionManager.removeAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        try {
            List<EmployeeDTO> employees = EMPLOYEE_SERVICE.findAll();
            if (employees == null || employees.isEmpty()) {
                return;
            }
            for (EmployeeDTO employee : employees) {
                if (employee == null || employee.getEmail() == null) {
                    continue;
                }
                String candidate = employee.getEmail().trim().toLowerCase(Locale.ROOT);
                if (normalized.equals(candidate) && !Boolean.FALSE.equals(employee.getActiveStatus())) {
                    SessionManager.setAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE, employee);
                    return;
                }
            }
        } catch (RentexpresException ex) {
            LOGGER.error("No se pudo determinar si {} es un empleado", email, ex);
        }
    }

    public static void refresh(HttpServletRequest request) {
        if (request == null) {
            return;
        }
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            SessionManager.removeAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
            return;
        }
        resolveFromEmail(request, currentUser.toString());
    }
}
