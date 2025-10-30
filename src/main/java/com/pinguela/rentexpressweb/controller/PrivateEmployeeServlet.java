package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.RoleDTO;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.RoleService;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpres.service.impl.RoleServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpres.service.HeadquartersService;
import com.pinguela.rentexpres.service.impl.HeadquartersServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Consola interna para revisar y filtrar el equipo de RentExpress.
 */
@WebServlet("/app/employees/private")
public class PrivateEmployeeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PrivateEmployeeServlet.class);

    private final EmployeeService employeeService = new EmployeeServiceImpl();
    private final RoleService roleService = new RoleServiceImpl();
    private final HeadquartersService headquartersService = new HeadquartersServiceImpl();

    public PrivateEmployeeServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        Object currentEmployee = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        if (currentUser == null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "Inicia sesión para acceder a la zona privada.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }
        if (currentEmployee == null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "No tienes permisos para consultar el panel de empleados.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.HOME_ENDPOINT);
            return;
        }

        disableCaching(response);
        exposeFlashMessages(request);

        Map<String, String> filters = buildFilters(request);
        Map<String, String> filterErrors = new LinkedHashMap<String, String>();

        Integer headquartersFilter = parseInteger(filters.get(EmployeeConstants.PARAM_HEADQUARTERS), filterErrors,
                "Selecciona una sede válida.");
        Boolean activeFilter = parseActive(filters.get(EmployeeConstants.PARAM_ACTIVE), filterErrors);

        List<EmployeeDTO> employees = loadEmployees();
        List<RoleDTO> roles = loadRoles();

        List<HeadquartersDTO> headquarters = loadHeadquarters();

        Map<Integer, String> roleNames = mapRoles(roles);
        Map<Integer, String> headquartersNames = mapHeadquarters(headquarters);

        List<EmployeeDTO> filtered = filterEmployees(employees, filters.get(EmployeeConstants.PARAM_SEARCH),
                headquartersFilter, activeFilter);

        Map<String, Object> summary = buildSummary(employees, filtered);

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Panel interno de empleados");
        request.setAttribute(EmployeeConstants.ATTR_FILTERS, filters);
        request.setAttribute(EmployeeConstants.ATTR_FILTER_ERRORS, filterErrors.values());
        request.setAttribute(EmployeeConstants.ATTR_EMPLOYEES, filtered);
        request.setAttribute(EmployeeConstants.ATTR_ROLE_NAMES, roleNames);
        request.setAttribute(EmployeeConstants.ATTR_HEADQUARTERS_NAMES, headquartersNames);
        request.setAttribute(EmployeeConstants.ATTR_SUMMARY, summary);

        request.getRequestDispatcher("/private/employee/employee_admin.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private Map<String, String> buildFilters(HttpServletRequest request) {
        Map<String, String> filters = new HashMap<String, String>();
        filters.put(EmployeeConstants.PARAM_SEARCH, trimToNull(request.getParameter(EmployeeConstants.PARAM_SEARCH)));
        filters.put(EmployeeConstants.PARAM_HEADQUARTERS,
                trimToNull(request.getParameter(EmployeeConstants.PARAM_HEADQUARTERS)));
        String active = request.getParameter(EmployeeConstants.PARAM_ACTIVE);
        if (active == null || active.trim().isEmpty()) {
            active = EmployeeConstants.VALUE_ALL;
        }
        filters.put(EmployeeConstants.PARAM_ACTIVE, active.trim().toLowerCase(Locale.ROOT));
        return filters;
    }

    private List<EmployeeDTO> loadEmployees() {
        try {
            List<EmployeeDTO> list = employeeService.findAll();
            if (list == null) {
                return Collections.emptyList();
            }
            return list;
        } catch (Exception ex) {
            LOGGER.error("No se pudieron recuperar los empleados", ex);
            return Collections.emptyList();
        }
    }

    private List<RoleDTO> loadRoles() {
        try {
            List<RoleDTO> roles = roleService.findAll();
            if (roles == null) {
                return Collections.emptyList();
            }
            return roles;
        } catch (Exception ex) {
            LOGGER.warn("No se pudieron recuperar los roles disponibles", ex);
            return Collections.emptyList();
        }
    }

    private List<HeadquartersDTO> loadHeadquarters() {
        try {
            List<HeadquartersDTO> headquarters = headquartersService.findAll();
            if (headquarters == null) {
                return Collections.emptyList();
            }
            return headquarters;
        } catch (Exception ex) {
            LOGGER.error("No se pudieron recuperar las sedes", ex);
            return Collections.emptyList();
        }
    }

    private Map<Integer, String> mapRoles(List<RoleDTO> roles) {
        Map<Integer, String> map = new LinkedHashMap<Integer, String>();
        for (int i = 0; i < roles.size(); i++) {
            RoleDTO role = roles.get(i);
            if (role != null && role.getRoleId() != null) {
                map.put(role.getRoleId(), role.getRoleName());
            }
        }
        return map;
    }

    private Map<Integer, String> mapHeadquarters(List<HeadquartersDTO> headquarters) {
        Map<Integer, String> map = new LinkedHashMap<Integer, String>();
        for (int i = 0; i < headquarters.size(); i++) {
            HeadquartersDTO dto = headquarters.get(i);
            if (dto != null && dto.getId() != null) {
                map.put(dto.getId(), dto.getName());
            }
        }
        return map;
    }

    private List<EmployeeDTO> filterEmployees(List<EmployeeDTO> employees, String search, Integer headquartersId,
            Boolean active) {
        List<EmployeeDTO> filtered = new ArrayList<EmployeeDTO>();
        for (int i = 0; i < employees.size(); i++) {
            EmployeeDTO employee = employees.get(i);
            if (employee == null) {
                continue;
            }
            if (!matchesSearch(employee, search)) {
                continue;
            }
            if (!matchesHeadquarters(employee, headquartersId)) {
                continue;
            }
            if (!matchesActive(employee, active)) {
                continue;
            }
            filtered.add(employee);
        }
        return filtered;
    }

    private boolean matchesSearch(EmployeeDTO employee, String search) {
        if (search == null || search.isEmpty()) {
            return true;
        }
        String normalized = search.toLowerCase(Locale.ROOT);
        if (contains(employee.getFirstName(), normalized)) {
            return true;
        }
        if (contains(employee.getLastName1(), normalized)) {
            return true;
        }
        if (contains(employee.getLastName2(), normalized)) {
            return true;
        }
        if (contains(employee.getEmail(), normalized)) {
            return true;
        }
        if (contains(employee.getEmployeeName(), normalized)) {
            return true;
        }
        return false;
    }

    private boolean matchesHeadquarters(EmployeeDTO employee, Integer headquartersId) {
        if (headquartersId == null) {
            return true;
        }
        return Objects.equals(employee.getHeadquartersId(), headquartersId);
    }

    private boolean matchesActive(EmployeeDTO employee, Boolean active) {
        if (active == null) {
            return true;
        }
        Boolean status = employee.getActiveStatus();
        if (status == null) {
            return !active.booleanValue();
        }
        return status.booleanValue() == active.booleanValue();
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private Map<String, Object> buildSummary(List<EmployeeDTO> all, List<EmployeeDTO> filtered) {
        Map<String, Object> summary = new LinkedHashMap<String, Object>();
        summary.put("total", Integer.valueOf(all.size()));
        summary.put("filtered", Integer.valueOf(filtered.size()));

        int activeCount = 0;
        int inactiveCount = 0;
        Set<Integer> headquarters = new HashSet<Integer>();

        for (int i = 0; i < all.size(); i++) {
            EmployeeDTO employee = all.get(i);
            if (employee == null) {
                continue;
            }
            if (Boolean.TRUE.equals(employee.getActiveStatus())) {
                activeCount++;
            } else {
                inactiveCount++;
            }
            if (employee.getHeadquartersId() != null) {
                headquarters.add(employee.getHeadquartersId());
            }
        }

        summary.put("active", Integer.valueOf(activeCount));
        summary.put("inactive", Integer.valueOf(inactiveCount));
        summary.put("headquarters", Integer.valueOf(headquarters.size()));
        return summary;
    }

    private Integer parseInteger(String value, Map<String, String> errors, String message) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            if (message != null && errors != null) {
                errors.put(EmployeeConstants.PARAM_HEADQUARTERS, message);
            }
            return null;
        }
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

    private void exposeFlashMessages(HttpServletRequest request) {
        Object success = SessionManager.getAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        if (success != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_SUCCESS, success);
            SessionManager.removeAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        }
        Object error = SessionManager.getAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        if (error != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, error);
            SessionManager.removeAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        }
    }

    private void disableCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
